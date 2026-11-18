package com.ucsal.viaaja.ia.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

public record AtividadeSugerida(
        String periodo,
        LocalTime horaInicio,
        LocalTime horaFim,
        String titulo,
        String descricao,
        BigDecimal custoEstimado) {
}
