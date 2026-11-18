package com.ucsal.viaaja.catalogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "quarto")
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String tipo;

    @Column(nullable = false)
    private int capacidade;

    @Column(name = "preco_diaria", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoDiaria;

    @ManyToOne
    @JoinColumn(name = "hospedagem_id")
    private Hospedagem hospedagem;

    protected Quarto() {
    }

    public Quarto(String tipo, int capacidade, BigDecimal precoDiaria) {
        this.tipo = tipo;
        this.capacidade = capacidade;
        this.precoDiaria = precoDiaria;
    }

    void vincular(Hospedagem hospedagem) {
        this.hospedagem = hospedagem;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public BigDecimal getPrecoDiaria() {
        return precoDiaria;
    }

    public Hospedagem getHospedagem() {
        return hospedagem;
    }
}
