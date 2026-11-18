package com.ucsal.viaaja.ia.dto;

import java.time.LocalDateTime;

public record RestricaoHorario(
        LocalDateTime inicio,
        LocalDateTime fim,
        String descricao) {
}
