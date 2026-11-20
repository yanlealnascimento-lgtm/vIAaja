package com.ucsal.viaaja.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResposta(
        LocalDateTime momento,
        int status,
        String erro,
        String mensagem,
        String regra,
        List<CampoInvalido> campos) {

    public static ErroResposta de(int status, String erro, String mensagem) {
        return new ErroResposta(LocalDateTime.now(), status, erro, mensagem, null, List.of());
    }

    public static ErroResposta de(int status, String erro, String mensagem, String regra) {
        return new ErroResposta(LocalDateTime.now(), status, erro, mensagem, regra, List.of());
    }

    public static ErroResposta validacao(int status, String mensagem, List<CampoInvalido> campos) {
        return new ErroResposta(LocalDateTime.now(), status, "Dados inválidos", mensagem, null, campos);
    }

    public record CampoInvalido(String campo, String mensagem) {
    }
}
