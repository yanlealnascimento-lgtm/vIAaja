package com.ucsal.viaaja.comparador.domain;

import com.ucsal.viaaja.catalogo.domain.ModalTransporte;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;

@Embeddable
public class OpcaoDeslocamento {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ModalTransporte modal;

    @Column(name = "custo_total", precision = 12, scale = 2)
    private BigDecimal custoTotal;

    @Column(nullable = false)
    private boolean estimado;

    @Column(nullable = false)
    private boolean disponivel;

    @Column(length = 300)
    private String detalhe;

    protected OpcaoDeslocamento() {
    }

    public OpcaoDeslocamento(ModalTransporte modal, BigDecimal custoTotal, boolean estimado,
                             boolean disponivel, String detalhe) {
        this.modal = modal;
        this.custoTotal = custoTotal;
        this.estimado = estimado;
        this.disponivel = disponivel;
        this.detalhe = detalhe;
    }

    public static OpcaoDeslocamento indisponivel(ModalTransporte modal, String motivo) {
        return new OpcaoDeslocamento(modal, null, false, false, motivo);
    }

    public ModalTransporte getModal() {
        return modal;
    }

    public BigDecimal getCustoTotal() {
        return custoTotal;
    }

    public boolean isEstimado() {
        return estimado;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public String getDetalhe() {
        return detalhe;
    }
}
