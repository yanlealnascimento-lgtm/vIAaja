package com.ucsal.viaaja.ia.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DadosGeracaoRoteiro(
        String destino,
        LocalDate dataInicio,
        LocalDate dataFim,
        BigDecimal orcamento,
        List<String> interesses,
        List<RestricaoHorario> restricoes,
        List<String> atividadesDisponiveis) {
}
