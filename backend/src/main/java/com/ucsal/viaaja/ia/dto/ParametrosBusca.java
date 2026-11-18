package com.ucsal.viaaja.ia.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ParametrosBusca(
        String origem,
        String destino,
        LocalDate dataInicio,
        LocalDate dataFim,
        BigDecimal orcamento,
        Integer passageiros,
        List<String> interesses,
        String interpretacao) {
}
