package com.ucsal.viaaja.identidade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class PreferenciaPrivacidade {

    @Column(name = "visivel_como_proximo", nullable = false)
    private boolean visivelComoProximo = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "quem_pode_convidar", nullable = false, length = 20)
    private QuemPodeConvidar quemPodeConvidar = QuemPodeConvidar.NINGUEM;

    public enum QuemPodeConvidar {
        TODOS,
        MESMO_DESTINO,
        NINGUEM
    }

    public boolean isVisivelComoProximo() {
        return visivelComoProximo;
    }

    public void setVisivelComoProximo(boolean visivelComoProximo) {
        this.visivelComoProximo = visivelComoProximo;
    }

    public QuemPodeConvidar getQuemPodeConvidar() {
        return quemPodeConvidar;
    }

    public void setQuemPodeConvidar(QuemPodeConvidar quemPodeConvidar) {
        this.quemPodeConvidar = quemPodeConvidar;
    }

    public boolean aceitaConviteDe(boolean mesmoDestino) {
        return switch (quemPodeConvidar) {
            case TODOS -> true;
            case MESMO_DESTINO -> mesmoDestino;
            case NINGUEM -> false;
        };
    }
}
