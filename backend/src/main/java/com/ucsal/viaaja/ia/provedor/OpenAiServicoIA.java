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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(name = "viaaja.ia.provedor", havingValue = "openai")
public class OpenAiServicoIA implements ServicoIA {

    private static final Logger log = LoggerFactory.getLogger(OpenAiServicoIA.class);
    private static final String IDIOMA = " Escreva todos os textos em portugues do Brasil, com acentuacao correta.";
    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RestTemplate restTemplate;
    private final PropriedadesAplicacao propriedades;
    private final ObjectMapper objectMapper;

    public OpenAiServicoIA(RestTemplate restTemplate, PropriedadesAplicacao propriedades, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.propriedades = propriedades;
        this.objectMapper = objectMapper;
    }

    @Override
    public String nome() {
        return "openai";
    }

    @Override
    public ParametrosBusca interpretarPedido(String textoLivre) {
        String instrucao = "Voce interpreta pedidos de viagem. Hoje e " + LocalDate.now() + ". "
                + "Responda apenas um JSON com as chaves origem, destino, dataInicio, dataFim, orcamento, "
                + "passageiros, interesses e interpretacao. As datas usam o formato AAAA-MM-DD e nunca ficam "
                + "no passado. Se o pedido citar apenas um mes, as datas ficam dentro desse mes. "
                + "Se o pedido citar a duracao em dias, dataFim e dataInicio somada a essa duracao menos um. "
                + "orcamento e um numero sem simbolo de moeda. interesses e uma lista de palavras curtas. "
                + "interpretacao e uma frase que resume o pedido. Use null para o que nao foi informado."
                + IDIOMA;
        JsonNode json = chamar(instrucao, textoLivre);
        Integer passageiros = inteiro(json, "passageiros");
        return new ParametrosBusca(
                texto(json, "origem"),
                texto(json, "destino"),
                data(json, "dataInicio"),
                data(json, "dataFim"),
                decimal(json, "orcamento"),
                passageiros != null && passageiros > 0 ? passageiros : 1,
                lista(json, "interesses"),
                texto(json, "interpretacao"));
    }

    @Override
    public RoteiroSugerido gerarItinerario(DadosGeracaoRoteiro dados) {
        String instrucao = "Voce monta roteiros de viagem dia a dia. Responda apenas um JSON com as chaves "
                + "titulo, destino, custoEstimadoTotal, observacao e dias. dias tem exatamente um item para "
                + "cada data entre dataInicio e dataFim. Cada item de dias tem ordem, data no formato AAAA-MM-DD "
                + "e atividades, com duas a quatro atividades por dia. Cada atividade tem periodo (MANHA, TARDE "
                + "ou NOITE), horaInicio e horaFim no formato HH:MM, titulo, descricao de uma frase e "
                + "custoEstimado em reais por pessoa, como numero sem simbolo de moeda. Prefira as atividades "
                + "da lista atividadesDisponiveis quando fizerem sentido, respeite o orcamento e nao proponha "
                + "nada que conflite com as restricoes de horario. observacao lembra que o roteiro e uma "
                + "sugestao e que precos e horarios devem ser confirmados." + IDIOMA;
        String pedido;
        try {
            pedido = objectMapper.writeValueAsString(dados);
        } catch (Exception ex) {
            log.warn("Falha ao montar o pedido de roteiro para a IA", ex);
            throw new ServicoIndisponivelException("IA", "Falha ao preparar o pedido: " + ex.getMessage());
        }

        JsonNode json = chamar(instrucao, pedido);
        List<DiaSugerido> dias = new ArrayList<>();
        JsonNode nosDias = json.path("dias");
        if (nosDias.isArray()) {
            for (JsonNode noDia : nosDias) {
                List<AtividadeSugerida> atividades = new ArrayList<>();
                JsonNode nosAtividades = noDia.path("atividades");
                if (nosAtividades.isArray()) {
                    for (JsonNode noAtividade : nosAtividades) {
                        String titulo = texto(noAtividade, "titulo");
                        if (titulo == null) {
                            continue;
                        }
                        atividades.add(new AtividadeSugerida(
                                cortar(periodo(texto(noAtividade, "periodo")), 20),
                                hora(noAtividade, "horaInicio"),
                                hora(noAtividade, "horaFim"),
                                cortar(titulo, 150),
                                cortar(texto(noAtividade, "descricao"), 600),
                                decimal(noAtividade, "custoEstimado")));
                    }
                }
                Integer ordem = inteiro(noDia, "ordem");
                LocalDate dataDia = data(noDia, "data");
                if (dataDia == null && dados.dataInicio() != null) {
                    dataDia = dados.dataInicio().plusDays(dias.size());
                }
                dias.add(new DiaSugerido(ordem != null ? ordem : dias.size() + 1, dataDia, atividades));
            }
        }

        if (dias.isEmpty()) {
            log.warn("A IA respondeu sem dias de roteiro: {}", json);
            throw new ServicoIndisponivelException("IA", "A resposta nao trouxe nenhum dia de roteiro");
        }

        return new RoteiroSugerido(cortar(texto(json, "titulo"), 150), texto(json, "destino"), dias,
                decimal(json, "custoEstimadoTotal"), texto(json, "observacao"));
    }

    @Override
    public ResumoAvaliacoes resumirAvaliacoes(String assunto, List<String> comentarios) {
        String instrucao = "Voce resume avaliacoes de viajantes. Responda apenas um JSON com as chaves "
                + "pontosPositivos e pontosNegativos, cada uma com no maximo quatro frases curtas. "
                + "Use somente o que os comentarios dizem, sem inventar." + IDIOMA;
        JsonNode json = chamar(instrucao, "Assunto: " + assunto + "\nComentarios: " + comentarios);
        return new ResumoAvaliacoes(lista(json, "pontosPositivos"), lista(json, "pontosNegativos"),
                comentarios.size(), LocalDateTime.now());
    }

    @Override
    public List<String> recomendarDestinos(List<String> historico, List<String> interesses) {
        String instrucao = "Voce recomenda destinos de viagem no Brasil. Responda apenas um JSON com a chave "
                + "destinos, uma lista com no maximo quatro nomes de cidades, sem repetir as do historico."
                + IDIOMA;
        JsonNode json = chamar(instrucao, "Historico: " + historico + "\nInteresses: " + interesses);
        return lista(json, "destinos");
    }

    @Override
    public String traduzir(String fala, String idiomaDestino) {
        String instrucao = "Voce traduz falas de uma conversa entre viajantes. Responda apenas um JSON com a "
                + "chave traducao, contendo o texto traduzido para o idioma pedido, sem comentarios.";
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

        String resposta;
        try {
            resposta = restTemplate.postForObject(config.getUrl(), new HttpEntity<>(corpo, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            log.warn("A API de IA respondeu {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ServicoIndisponivelException("IA", "A API de IA respondeu " + ex.getStatusCode());
        } catch (Exception ex) {
            log.warn("Falha de comunicacao com a API de IA", ex);
            throw new ServicoIndisponivelException("IA", "Falha ao consultar a API de IA: " + ex.getMessage());
        }

        try {
            JsonNode raiz = objectMapper.readTree(resposta);
            String conteudo = raiz.path("choices").path(0).path("message").path("content").asText("");
            return objectMapper.readTree(extrairJson(conteudo));
        } catch (Exception ex) {
            log.warn("A API de IA devolveu um conteudo que nao e JSON valido: {}", resposta);
            throw new ServicoIndisponivelException("IA", "A resposta da IA veio em formato inesperado");
        }
    }

    private String cortar(String valor, int limite) {
        if (valor == null || valor.length() <= limite) {
            return valor;
        }
        return valor.substring(0, limite - 3).trim() + "...";
    }

    private String extrairJson(String conteudo) {
        int inicio = conteudo.indexOf('{');
        int fim = conteudo.lastIndexOf('}');
        if (inicio >= 0 && fim > inicio) {
            return conteudo.substring(inicio, fim + 1);
        }
        return conteudo;
    }

    private String texto(JsonNode no, String campo) {
        JsonNode valor = no.get(campo);
        if (valor == null || valor.isNull()) {
            return null;
        }
        String texto = valor.asText().trim();
        return texto.isEmpty() || texto.equalsIgnoreCase("null") ? null : texto;
    }

    private Integer inteiro(JsonNode no, String campo) {
        String valor = texto(no, campo);
        if (valor == null) {
            return null;
        }
        try {
            return Integer.parseInt(valor.replaceAll("[^0-9-]", ""));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private BigDecimal decimal(JsonNode no, String campo) {
        String valor = texto(no, campo);
        if (valor == null) {
            return null;
        }
        String limpo = valor.replaceAll("[^0-9,.-]", "");
        if (limpo.contains(",") && limpo.contains(".")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        } else if (limpo.contains(",")) {
            limpo = limpo.replace(",", ".");
        }
        try {
            return limpo.isEmpty() ? null : new BigDecimal(limpo);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private LocalDate data(JsonNode no, String campo) {
        String valor = texto(no, campo);
        if (valor == null) {
            return null;
        }
        try {
            return LocalDate.parse(valor.length() > 10 ? valor.substring(0, 10) : valor);
        } catch (Exception ex) {
            try {
                return LocalDate.parse(valor, DATA_BR);
            } catch (Exception ignorada) {
                return null;
            }
        }
    }

    private LocalTime hora(JsonNode no, String campo) {
        String valor = texto(no, campo);
        if (valor == null) {
            return null;
        }
        String[] partes = valor.replaceAll("[^0-9:]", "").split(":");
        try {
            int horas = Integer.parseInt(partes[0]);
            int minutos = partes.length > 1 ? Integer.parseInt(partes[1]) : 0;
            return LocalTime.of(Math.min(horas, 23), Math.min(minutos, 59));
        } catch (Exception ex) {
            return null;
        }
    }

    private String periodo(String valor) {
        if (valor == null) {
            return null;
        }
        String normalizado = valor.trim().toUpperCase()
                .replace("\u00C3", "A").replace("\u00C1", "A").replace("\u00C9", "E");
        if (normalizado.startsWith("MAN")) {
            return "MANHA";
        }
        if (normalizado.startsWith("TAR")) {
            return "TARDE";
        }
        if (normalizado.startsWith("NOI")) {
            return "NOITE";
        }
        return normalizado;
    }

    private List<String> lista(JsonNode no, String campo) {
        List<String> valores = new ArrayList<>();
        JsonNode itens = no.get(campo);
        if (itens != null && itens.isArray()) {
            itens.forEach(item -> {
                String texto = item.asText().trim();
                if (!texto.isEmpty()) {
                    valores.add(texto);
                }
            });
        }
        return valores;
    }
}
