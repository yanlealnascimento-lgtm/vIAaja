package com.ucsal.viaaja.catalogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "oferta")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false, length = 3)
    private String moeda = "BRL";

    @Column(nullable = false)
    private int disponibilidade;

    @Column(nullable = false)
    private boolean ativa = true;

    @ManyToOne
    @JoinColumn(name = "parceiro_id")
    private com.ucsal.viaaja.identidade.domain.ParceiroFornecedor parceiro;

    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    private Destino destino;

    protected Oferta() {
    }

    protected Oferta(BigDecimal preco, int disponibilidade, Destino destino,
                     com.ucsal.viaaja.identidade.domain.ParceiroFornecedor parceiro) {
        this.preco = preco;
        this.disponibilidade = disponibilidade;
        this.destino = destino;
        this.parceiro = parceiro;
    }

    public boolean temDisponibilidade(int quantidade) {
        return ativa && disponibilidade >= quantidade;
    }

    public void atualizarPreco(BigDecimal novoPreco) {
        this.preco = novoPreco;
    }

    public void atualizarDisponibilidade(int novaDisponibilidade) {
        this.disponibilidade = novaDisponibilidade;
    }

    public void desativar() {
        this.ativa = false;
    }

    public void ativar() {
        this.ativa = true;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public String getMoeda() {
        return moeda;
    }

    public int getDisponibilidade() {
        return disponibilidade;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public com.ucsal.viaaja.identidade.domain.ParceiroFornecedor getParceiro() {
        return parceiro;
    }

    public Destino getDestino() {
        return destino;
    }
}
