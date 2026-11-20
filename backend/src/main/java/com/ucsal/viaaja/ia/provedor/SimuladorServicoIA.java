package com.ucsal.viaaja.ia.provedor;

import com.ucsal.viaaja.ia.ServicoIA;
import com.ucsal.viaaja.ia.dto.AtividadeSugerida;
import com.ucsal.viaaja.ia.dto.DadosGeracaoRoteiro;
import com.ucsal.viaaja.ia.dto.DiaSugerido;
import com.ucsal.viaaja.ia.dto.ParametrosBusca;
import com.ucsal.viaaja.ia.dto.RestricaoHorario;
import com.ucsal.viaaja.ia.dto.ResumoAvaliacoes;
import com.ucsal.viaaja.ia.dto.RoteiroSugerido;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "viaaja.ia.provedor", havingValue = "simulado", matchIfMissing = true)
public class SimuladorServicoIA implements ServicoIA {

    private static final Pattern PADRAO_ORCAMENTO = Pattern.compile("(\\d{3,6})");
    private static final List<String> MESES = List.of("janeiro", "fevereiro", "marco", "abril", "maio", "junho",
            "julho", "agosto", "setembro", "outubro", "novembro", "dezembro");
    private static final List<String> INTERESSES = List.of("gastronomia", "natureza", "cultura",
            "vida noturna", "praia", "aventura", "familia", "historia", "compras");

    @Override
    public String nome() {
        return "simulado";
    }

    @Override
    public ParametrosBusca interpretarPedido(String textoLivre) {
        String texto = Normalizer.normalize(textoLivre, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("(\\d)\\.(\\d{3})", "$1$2")
                .toLowerCase(Locale.ROOT);

        BigDecimal orcamento = null;
        Matcher matcher = PADRAO_ORCAMENTO.matcher(texto);
        if (matcher.find()) {
            orcamento = new BigDecimal(matcher.group(1));
        }

        LocalDate inicio = LocalDate.now().plusDays(30);
        for (int i = 0; i < MESES.size(); i++) {
            if (texto.contains(MESES.get(i))) {
                LocalDate candidata = LocalDate.now().withMonth(i + 1).withDayOfMonth(1);
                inicio = candidata.isBefore(LocalDate.now()) ? candidata.plusYears(1) : candidata;
                break;
            }
        }

        int pessoas = texto.contains("duas pessoas") || texto.contains("casal") ? 2
                : texto.contains("familia") ? 4 : 1;

        List<String> interesses = INTERESSES.stream().filter(texto::contains).toList();
        if (interesses.isEmpty()) {
            interesses = List.of("cultura", "gastronomia");
        }

        String destino = extrairDestino(texto);

        return new ParametrosBusca(null, destino, inicio, inicio.plusDays(4), orcamento, pessoas,
                interesses, "Pedido interpretado pelo simulador local a partir do texto livre");
    }

    @Override
    public RoteiroSugerido gerarItinerario(DadosGeracaoRoteiro dados) {
        long totalDias = ChronoUnit.DAYS.between(dados.dataInicio(), dados.dataFim()) + 1;
        List<DiaSugerido> dias = new ArrayList<>();
        BigDecimal acumulado = BigDecimal.ZERO;

        List<String> base = dados.atividadesDisponiveis() == null || dados.atividadesDisponiveis().isEmpty()
                ? List.of("Centro histórico", "Mercado local", "Mirante da cidade", "Museu principal",
                        "Parque urbano", "Praia ou orla", "Restaurante típico", "Feira de artesanato")
                : dados.atividadesDisponiveis();

        for (int i = 0; i < totalDias; i++) {
            LocalDate data = dados.dataInicio().plusDays(i);
            List<AtividadeSugerida> atividades = new ArrayList<>();

            boolean manhaBloqueada = horarioBloqueado(dados.restricoes(), data, LocalTime.of(9, 0));
            boolean noiteBloqueada = horarioBloqueado(dados.restricoes(), data, LocalTime.of(19, 0));

            if (!manhaBloqueada) {
                String titulo = base.get((i * 2) % base.size());
                BigDecimal custo = BigDecimal.valueOf(45 + (i * 7) % 60);
                atividades.add(new AtividadeSugerida("MANHA", LocalTime.of(9, 0), LocalTime.of(12, 0),
                        titulo, "Visita guiada em " + titulo + " com tempo livre para fotos", custo));
                acumulado = acumulado.add(custo);
            }

            String tituloTarde = base.get((i * 2 + 1) % base.size());
            BigDecimal custoTarde = BigDecimal.valueOf(60 + (i * 11) % 70);
            atividades.add(new AtividadeSugerida("TARDE", LocalTime.of(14, 0), LocalTime.of(17, 30),
                    tituloTarde, "Programa de tarde em " + tituloTarde, custoTarde));
            acumulado = acumulado.add(custoTarde);

            if (!noiteBloqueada && i % 2 == 0) {
                BigDecimal custoNoite = BigDecimal.valueOf(90);
                atividades.add(new AtividadeSugerida("NOITE", LocalTime.of(19, 30), LocalTime.of(22, 0),
                        "Jantar típico", "Sugestão de restaurante da culinária local", custoNoite));
                acumulado = acumulado.add(custoNoite);
            }

            dias.add(new DiaSugerido(i + 1, data, atividades));
        }

        String observacao = "Sugestão gerada automaticamente. Confira horários e preços antes de viajar.";
        if (dados.orcamento() != null && acumulado.compareTo(dados.orcamento()) > 0) {
            observacao = observacao + " O custo estimado passa do orçamento informado.";
        }

        return new RoteiroSugerido("Roteiro para " + dados.destino(), dados.destino(), dias,
                acumulado, observacao);
    }

    @Override
    public ResumoAvaliacoes resumirAvaliacoes(String assunto, List<String> comentarios) {
        List<String> positivos = new ArrayList<>();
        List<String> negativos = new ArrayList<>();

        List<String> marcasPositivas = List.of("otimo", "bom", "excelente", "limpo", "perto", "recomendo",
                "agradavel", "rapido", "barato");
        List<String> marcasNegativas = List.of("ruim", "sujo", "caro", "longe", "demorado", "barulho",
                "pessimo", "fila", "lento");

        for (String comentario : comentarios) {
            String texto = comentario.toLowerCase(Locale.ROOT);
            if (marcasPositivas.stream().anyMatch(texto::contains) && positivos.size() < 4) {
                positivos.add(resumirFrase(comentario));
            }
            if (marcasNegativas.stream().anyMatch(texto::contains) && negativos.size() < 4) {
                negativos.add(resumirFrase(comentario));
            }
        }

        if (positivos.isEmpty()) {
            positivos.add("Avaliações majoritariamente neutras sobre " + assunto);
        }
        if (negativos.isEmpty()) {
            negativos.add("Nenhum ponto negativo recorrente identificado");
        }

        return new ResumoAvaliacoes(positivos, negativos, comentarios.size(), LocalDateTime.now());
    }

    @Override
    public List<String> recomendarDestinos(List<String> historico, List<String> interesses) {
        List<String> catalogo = List.of("Salvador", "Porto Seguro", "Lençóis", "Morro de São Paulo",
                "Chapada Diamantina", "Ilhéus", "Recife", "Fortaleza", "Rio de Janeiro");
        return catalogo.stream()
                .filter(destino -> historico == null || !historico.contains(destino))
                .limit(4)
                .toList();
    }

    @Override
    public String traduzir(String fala, String idiomaDestino) {
        return "[" + idiomaDestino + "] " + fala;
    }

    private boolean horarioBloqueado(List<RestricaoHorario> restricoes, LocalDate data, LocalTime hora) {
        if (restricoes == null || restricoes.isEmpty()) {
            return false;
        }
        LocalDateTime momento = LocalDateTime.of(data, hora);
        return restricoes.stream().anyMatch(r ->
                !momento.isBefore(r.inicio().minusHours(2)) && !momento.isAfter(r.fim().plusHours(2)));
    }

    private String resumirFrase(String comentario) {
        String limpo = comentario.trim();
        return limpo.length() <= 110 ? limpo : limpo.substring(0, 107) + "...";
    }

    private String extrairDestino(String texto) {
        Map<String, String> cidades = new LinkedHashMap<>();
        cidades.put("morro de sao paulo", "Morro de São Paulo");
        cidades.put("chapada diamantina", "Lençóis");
        cidades.put("porto seguro", "Porto Seguro");
        cidades.put("rio de janeiro", "Rio de Janeiro");
        cidades.put("salvador", "Salvador");
        cidades.put("lencois", "Lençóis");
        cidades.put("chapada", "Lençóis");
        cidades.put("ilheus", "Ilhéus");
        cidades.put("recife", "Recife");
        cidades.put("fortaleza", "Fortaleza");
        for (Map.Entry<String, String> cidade : cidades.entrySet()) {
            if (texto.contains(cidade.getKey())) {
                return cidade.getValue();
            }
        }
        if (texto.contains("litoral") || texto.contains("praia")) {
            return "Porto Seguro";
        }
        if (texto.contains("serra") || texto.contains("montanha") || texto.contains("trilha")) {
            return "Lençóis";
        }
        return "Salvador";
    }

}
