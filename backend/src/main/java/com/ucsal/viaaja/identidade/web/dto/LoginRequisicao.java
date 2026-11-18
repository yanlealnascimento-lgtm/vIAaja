package com.ucsal.viaaja.identidade.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequisicao(
        @NotBlank @Email String email,
        @NotBlank String senha) {
}
