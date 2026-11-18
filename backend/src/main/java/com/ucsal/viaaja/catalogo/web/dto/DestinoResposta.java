package com.ucsal.viaaja.catalogo.web.dto;

import com.ucsal.viaaja.catalogo.domain.Destino;

public record DestinoResposta(
        Long id,
        String cidade,
        String estado,
        String pais,
        String descricao,
        Integer distanciaReferenciaKm) {

    public static DestinoResposta de(Destino d) {
        return new DestinoResposta(d.getId(), d.getCidade(), d.getEstado(), d.getPais(),
                d.getDescricao(), d.getDistanciaReferenciaKm());
    }
}
