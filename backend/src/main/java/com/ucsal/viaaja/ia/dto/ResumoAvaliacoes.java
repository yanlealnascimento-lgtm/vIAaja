package com.ucsal.viaaja.ia.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ResumoAvaliacoes(
        List<String> pontosPositivos,
        List<String> pontosNegativos,
        int totalAvaliacoesConsideradas,
        LocalDateTime geradoEm) {
}
