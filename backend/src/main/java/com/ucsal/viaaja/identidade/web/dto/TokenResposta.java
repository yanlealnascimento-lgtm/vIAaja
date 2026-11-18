package com.ucsal.viaaja.identidade.web.dto;

public record TokenResposta(
        String token,
        String tipo,
        long expiraEmSegundos,
        Long usuarioId,
        String nome,
        String perfil) {
}
