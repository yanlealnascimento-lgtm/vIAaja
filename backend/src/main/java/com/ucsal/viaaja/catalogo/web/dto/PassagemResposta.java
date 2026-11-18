package com.ucsal.viaaja.catalogo.web.dto;

import com.ucsal.viaaja.catalogo.domain.Passagem;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PassagemResposta(
        Long id,
        String modal,
        String empresa,
        String origem,
        String destino,
        LocalDateTime dataPartida,
        LocalDateTime dataChegada,
        long duracaoMinutos,
        int numeroParadas,
        int assentosDisponiveis,
        BigDecimal preco) {

    public static PassagemResposta de(Passagem p) {
        return new PassagemResposta(p.getId(), p.getModal().name(), p.getEmpresa(), p.getOrigem(),
                p.getNomeDestino(), p.getDataPartida(), p.getDataChegada(), p.duracaoEmMinutos(),
                p.getNumeroParadas(), p.getAssentosDisponiveis(), p.getPreco());
    }
}
