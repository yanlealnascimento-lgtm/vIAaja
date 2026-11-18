package com.ucsal.viaaja.ia.dto;

import java.time.LocalDate;
import java.util.List;

public record DiaSugerido(
        int ordem,
        LocalDate data,
        List<AtividadeSugerida> atividades) {
}
