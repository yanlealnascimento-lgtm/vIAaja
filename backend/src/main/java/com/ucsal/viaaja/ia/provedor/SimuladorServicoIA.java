package com.ucsal.viaaja.ia.provedor;

import com.ucsal.viaaja.ia.ServicoIA;
import com.ucsal.viaaja.ia.dto.AtividadeSugerida;
import com.ucsal.viaaja.ia.dto.DadosGeracaoRoteiro;
import com.ucsal.viaaja.ia.dto.DiaSugerido;
import com.ucsal.viaaja.ia.dto.ParametrosBusca;
import com.ucsal.viaaja.ia.dto.ResumoAvaliacoes;
import com.ucsal.viaaja.ia.dto.RestricaoHorario;
import com.ucsal.viaaja.ia.dto.RoteiroSugerido;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
        String texto = textoLivre.toLowerCase(Locale.ROOT);

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
                interesses, "Pedido interpretado pelo provedor simulado a partir do texto livre");
    }

    @Override
    public RoteiroSugerido gerarItinerario(DadosGeracaoRoteiro dados) {
        long totalDias = ChronoUnit.DAYS.between(dados.dataInicio(), dados.dataFim()) + 1;
        List<DiaSugerido> dias = new ArrayList<>();
        BigDecimal acumulado = BigDecimal.ZERO;

        List<String> base = dados.atividadesDisponiveis() == null || dados.atividadesDisponiveis().isEmpty()
                ? List.of("Centro historico", "Mercado local", "Mirante da cidade", "Museu principal",
                        "Parque urbano", "Praia ou orla", "Restaurante tipico", "Feira de artesanato")
                : dados.atividadesDisponiveis();

        for (int i = 0; i < totalDias; i++) {
            LocalDate data = dados.dataInicio().plusDays(i);
            List<AtividadeSugerida> atividades = new ArrayList<>();

            boolean manhaBloqueada = horarioBloqueado(dados.restricoes(), data, LocalTime.of(9, 0));
            boolean noiteBloqueada = horarioBloqueado(dados.restricoes(), data, LocalTime.of(19, 0));

            if (!manhaBloqueada) {
                String titulo = base.get((i * 2) % base.size());
                BigDecimal custo = BigDecimal.valueOf(45 + (i * 7) % 60);
                atividades.add(new AtividadeSugerida("Manha", LocalTime.of(9, 0), LocalTime.of(12, 0),
                        titulo, "Visita guiada em " + titulo + " com tempo livre para fotos", custo));
                acumulado = acumulado.add(custo);
            }

            String tituloTarde = base.get((i * 2 + 1) % base.size());
            BigDecimal custoTarde = BigDecimal.valueOf(60 + (i * 11) % 70);
            atividades.add(new AtividadeSugerida("Tarde", LocalTime.of(14, 0), LocalTime.of(17, 30),
                    tituloTarde, "Programa de tarde em " + tituloTarde, custoTarde));
            acumulado = acumulado.add(custoTarde);

            if (!noiteBloqueada && i % 2 == 0) {
                BigDecimal custoNoite = BigDecimal.valueOf(90);
                atividades.add(new AtividadeSugerida("Noite", LocalTime.of(19, 30), LocalTime.of(22, 0),
                        "Jantar tipico", "Sugestao de restaurante da culinaria local", custoNoite));
                acumulado = acumulado.add(custoNoite);
            }

            dias.add(new DiaSugerido(i + 1, data, atividades));
        }

        String observacao = "Sugestao gerada automaticamente. Revise antes de salvar (RN-008).";
        if (dados.orcamento() != null && acumulado.compareTo(dados.orcamento()) > 0) {
            observacao = observacao + " O custo estimado ultrapassa o orcamento informado.";
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
            positivos.add("Avaliacoes majoritariamente neutras sobre " + assunto);
        }
        if (negativos.isEmpty()) {
            negativos.add("Nenhum ponto negativo recorrente identificado");
        }

        return new ResumoAvaliacoes(positivos, negativos, comentarios.size(), LocalDateTime.now());
    }

    @Override
    public List<String> recomendarDestinos(List<String> historico, List<String> interesses) {
        List<String> catalogo = List.of("Salvador", "Porto Seguro", "Lencois", "Morro de Sao Paulo",
                "Chapada Diamantina", "Ilheus", "Recife", "Fortaleza", "Rio de Janeiro");
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
        List<String> cidades = Arrays.asList("salvador", "porto seguro", "lencois", "morro de sao paulo",
                "chapada diamantina", "ilheus", "recife", "fortaleza", "rio de janeiro");
        for (String cidade : cidades) {
            if (texto.contains(cidade)) {
                return capitalizar(cidade);
            }
        }
        if (texto.contains("litoral") || texto.contains("praia")) {
            return "Porto Seguro";
        }
        if (texto.contains("serra") || texto.contains("montanha") || texto.contains("trilha")) {
            return "Lencois";
        }
        return "Salvador";
    }

    private String capitalizar(String texto) {
        String[] partes = texto.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String parte : partes) {
            sb.append(Character.toUpperCase(parte.charAt(0))).append(parte.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
