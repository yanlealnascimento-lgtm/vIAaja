package com.ucsal.viaaja.catalogo.web.dto;

import com.ucsal.viaaja.catalogo.domain.Hospedagem;
import java.math.BigDecimal;
import java.util.List;

public record HospedagemResposta(
        Long id,
        String nome,
        int categoria,
        String cidade,
        String endereco,
        List<String> comodidades,
        int disponibilidade,
        BigDecimal precoDiaria,
        List<QuartoResposta> quartos) {

    public record QuartoResposta(Long id, String tipo, int capacidade, BigDecimal precoDiaria) {
    }

    public static HospedagemResposta de(Hospedagem h) {
        return new HospedagemResposta(h.getId(), h.getNome(), h.getCategoria(),
                h.getDestino().getCidade(), h.getEndereco(), h.getComodidades(), h.getDisponibilidade(),
                h.getPreco(),
                h.getQuartos().stream()
                        .map(q -> new QuartoResposta(q.getId(), q.getTipo(), q.getCapacidade(), q.getPrecoDiaria()))
                        .toList());
    }
}
