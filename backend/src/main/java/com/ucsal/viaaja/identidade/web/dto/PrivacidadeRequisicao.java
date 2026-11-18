package com.ucsal.viaaja.identidade.web.dto;

import com.ucsal.viaaja.identidade.domain.PreferenciaPrivacidade;
import jakarta.validation.constraints.NotNull;

public record PrivacidadeRequisicao(
        @NotNull Boolean visivelComoProximo,
        @NotNull PreferenciaPrivacidade.QuemPodeConvidar quemPodeConvidar) {
}
