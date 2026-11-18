package com.ucsal.viaaja.identidade.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilRequisicao(
        @NotBlank @Size(min = 3, max = 150) String nome,
        @Size(max = 20) String telefone,
        @Size(max = 120) String cidadeAproximada) {
}
