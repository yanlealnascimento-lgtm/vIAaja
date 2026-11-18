package com.ucsal.viaaja.catalogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "destino")
public class Destino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(nullable = false, length = 60)
    private String pais;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "distancia_referencia_km")
    private Integer distanciaReferenciaKm;

    protected Destino() {
    }

    public Destino(String cidade, String estado, String pais, String descricao, Integer distanciaReferenciaKm) {
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.descricao = descricao;
        this.distanciaReferenciaKm = distanciaReferenciaKm;
    }

    public String nomeCompleto() {
        return cidade + "/" + estado;
    }

    public Long getId() {
        return id;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getPais() {
        return pais;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getDistanciaReferenciaKm() {
        return distanciaReferenciaKm;
    }
}
