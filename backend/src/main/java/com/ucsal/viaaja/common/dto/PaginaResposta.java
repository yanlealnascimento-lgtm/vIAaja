package com.ucsal.viaaja.common.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PaginaResposta<T>(
        List<T> conteudo,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas,
        boolean ultima) {

    public static <T> PaginaResposta<T> de(Page<T> page) {
        return new PaginaResposta<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
