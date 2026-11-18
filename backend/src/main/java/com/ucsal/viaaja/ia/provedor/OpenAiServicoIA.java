package com.ucsal.viaaja.ia.provedor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucsal.viaaja.common.exception.ServicoIndisponivelException;
import com.ucsal.viaaja.config.PropriedadesAplicacao;
import com.ucsal.viaaja.ia.ServicoIA;
import com.ucsal.viaaja.ia.dto.AtividadeSugerida;
import com.ucsal.viaaja.ia.dto.DadosGeracaoRoteiro;
import com.ucsal.viaaja.ia.dto.DiaSugerido;
import com.ucsal.viaaja.ia.dto.ParametrosBusca;
import com.ucsal.viaaja.ia.dto.ResumoAvaliacoes;
import com.ucsal.viaaja.ia.dto.RoteiroSugerido;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(name = "viaaja.ia.provedor", havingValue = "openai")
public class OpenAiServicoIA implements ServicoIA {

    private final RestTemplate restTemplate;
    private final PropriedadesAplicacao propriedades;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAiServicoIA(RestTemplate restTemplate, PropriedadesAplicacao propriedades) {
        this.restTemplate = restTemplate;
        this.propriedades = propriedades;
    }

    @Override
    public String nome() {
        return "openai";
    }

    @Override
    public ParametrosBusca interpretarPedido(String textoLivre) {
        String instrucao = "Voce interpreta pedidos de viagem. Responda apenas um JSON com as chaves "
                + "destino, dataInicio, dataFim, orcamento, passageiros, interesses, interpretacao. "
                + "As datas usam o formato AAAA-MM-DD e interesses e uma lista de textos curtos.";
        JsonNode json = chamar(instrucao, textoLivre);
        return new ParametrosBusca(
                texto(json, "origem"),
                texto(json, "destino"),
                data(json, "dataInicio"),
                data(json, "dataFim"),
                decimal(json, "orcamento"),
                json.hasNonNull("passageiros") ? json.get("passageiros").asInt() : 1,
                lista(json, "interesses"),
                texto(json, "interpretacao"));
    }

    @Override
    public RoteiroSugerido gerarItinerario(DadosGeracaoRoteiro dados) {
        String instrucao = "Voce monta roteiros de viagem dia a dia. Responda apenas um JSON com as chaves "
                + "titulo, destino, custoEstimadoTotal, observacao e dias. Cada item de dias tem ordem, data "
                + "no formato AAAA-MM-DD e atividades. Cada atividade tem periodo, horaInicio, horaFim no "
                + "formato HH:MM, titulo, descricao e custoEstimado. Nao proponha atividades que conflitem "
                + "com as restricoes de horario informadas.";
        String pedido;
        try {
            pedido = objectMapper.writeValueAsString(dados);
        } catch (Exception ex) {
            throw new ServicoIndisponivelException("IA", "Falha ao preparar o pedido: " + ex.getMessage());
        }

        JsonNode json = chamar(instrucao, pedido);
        List<DiaSugerido> dias = new ArrayList<>();
        if (json.has("dias")) {
            for (JsonNode noDia : json.get("dias")) {
                List<AtividadeSugerida> atividades = new ArrayList<>();
                if (noDia.has("atividades")) {
                    for (JsonNode noAtividade : noDia.get("atividades")) {
                        atividades.add(new AtividadeSugerida(
                                texto(noAtividade, "periodo"),
                                hora(noAtividade, "horaInicio"),
                                hora(noAtividade, "horaFim"),
                                texto(noAtividade, "titulo"),
                                texto(noAtividade, "descricao"),
                                decimal(noAtividade, "custoEstimado")));
                    }
                }
                dias.add(new DiaSugerido(
                        noDia.hasNonNull("ordem") ? noDia.get("ordem").asInt() : dias.size() + 1,
                        data(noDia, "data"),
                        atividades));
            }
        }

        if (dias.isEmpty()) {
            throw new ServicoIndisponivelException("IA", "A resposta nao trouxe nenhum dia de roteiro");
        }

        return new RoteiroSugerido(texto(json, "titulo"), texto(json, "destino"), dias,
                decimal(json, "custoEstimadoTotal"), texto(json, "observacao"));
    }

    @Override
    public ResumoAvaliacoes resumirAvaliacoes(String assunto, List<String> comentarios) {
        String instrucao = "Resuma avaliacoes de viajantes. Responda apenas um JSON com as chaves "
                + "pontosPositivos e pontosNegativos, cada uma com no maximo quatro textos curtos.";
        JsonNode json = chamar(instrucao, "Assunto: " + assunto + "\nComentarios: " + comentarios);
        return new ResumoAvaliacoes(lista(json, "pontosPositivos"), lista(json, "pontosNegativos"),
                comentarios.size(), LocalDateTime.now());
    }

    @Override
    public List<String> recomendarDestinos(List<String> historico, List<String> interesses) {
        String instrucao = "Recomende destinos de viagem no Brasil. Responda apenas um JSON com a chave "
                + "destinos, contendo no maximo quatro nomes de cidades.";
        JsonNode json = chamar(instrucao, "Historico: " + historico + "\nInteresses: " + interesses);
        return lista(json, "destinos");
    }

    @Override
    public String traduzir(String fala, String idiomaDestino) {
        String instrucao = "Traduza o texto. Responda apenas um JSON com a chave traducao.";
        JsonNode json = chamar(instrucao, "Idioma de destino: " + idiomaDestino + "\nTexto: " + fala);
        return texto(json, "traducao");
    }

    private JsonNode chamar(String instrucao, String conteudoUsuario) {
        var config = propriedades.getIa();
        if (config.getChave() == null || config.getChave().isBlank()) {
            throw new ServicoIndisponivelException("IA", "Chave da API de IA nao configurada");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getChave());

        Map<String, Object> corpo = Map.of(
                "model", config.getModelo(),
                "temperature", 0.4,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", instrucao),
                        Map.of("role", "user", "content", conteudoUsuario)));

        try {
            String resposta = restTemplate.postForObject(config.getUrl(),
                    new HttpEntity<>(corpo, headers), String.class);
            JsonNode raiz = objectMapper.readTree(resposta);
            String conteudo = raiz.path("choices").path(0).path("message").path("content").asText();
            return objectMapper.readTree(conteudo);
        } catch (Exception ex) {
            throw new ServicoIndisponivelException("IA", "Falha ao consultar a API de IA: " + ex.getMessage());
        }
    }

    private String texto(JsonNode no, String campo) {
        return no.hasNonNull(campo) ? no.get(campo).asText() : null;
    }

    private BigDecimal decimal(JsonNode no, String campo) {
        return no.hasNonNull(campo) ? new BigDecimal(no.get(campo).asText()) : null;
    }

    private LocalDate data(JsonNode no, String campo) {
        return no.hasNonNull(campo) ? LocalDate.parse(no.get(campo).asText()) : null;
    }

    private LocalTime hora(JsonNode no, String campo) {
        return no.hasNonNull(campo) ? LocalTime.parse(no.get(campo).asText()) : null;
    }

    private List<String> lista(JsonNode no, String campo) {
        List<String> valores = new ArrayList<>();
        if (no.has(campo) && no.get(campo).isArray()) {
            no.get(campo).forEach(item -> valores.add(item.asText()));
        }
        return valores;
    }
}
