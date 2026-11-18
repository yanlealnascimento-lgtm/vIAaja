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
@Table(name = "atividade")
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 60)
    private String categoria;

    @Column(name = "custo_medio", precision = 12, scale = 2)
    private BigDecimal custoMedio;

    @ManyToOne
    @JoinColumn(name = "destino_id")
    private Destino destino;

    protected Atividade() {
    }

    public Atividade(String nome, String categoria, BigDecimal custoMedio, Destino destino) {
        this.nome = nome;
        this.categoria = categoria;
        this.custoMedio = custoMedio;
        this.destino = destino;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public BigDecimal getCustoMedio() {
        return custoMedio;
    }

    public Destino getDestino() {
        return destino;
    }
}
