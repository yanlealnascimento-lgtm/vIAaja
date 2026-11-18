package com.ucsal.viaaja.ia.dto;

import java.math.BigDecimal;
import java.util.List;

public record RoteiroSugerido(
        String titulo,
        String destino,
        List<DiaSugerido> dias,
        BigDecimal custoEstimadoTotal,
        String observacao) {
}
