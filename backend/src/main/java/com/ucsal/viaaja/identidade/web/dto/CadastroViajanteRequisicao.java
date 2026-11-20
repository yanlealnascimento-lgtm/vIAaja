package com.ucsal.viaaja.identidade.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CadastroViajanteRequisicao(
        @NotBlank @Size(min = 3, max = 150) String nome,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String senha,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos") String cpf,
        @NotBlank @Size(min = 10, max = 20) String telefone,
        @NotNull @Past LocalDate dataNascimento,
        String cidadeAproximada) {
}
