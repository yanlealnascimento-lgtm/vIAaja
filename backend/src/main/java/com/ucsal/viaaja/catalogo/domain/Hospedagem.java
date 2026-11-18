package com.ucsal.viaaja.catalogo.domain;

import com.ucsal.viaaja.identidade.domain.ParceiroFornecedor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hospedagem")
@PrimaryKeyJoinColumn(name = "oferta_id")
public class Hospedagem extends Oferta {

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false)
    private int categoria;

    @Column(length = 250)
    private String endereco;

    @ElementCollection
    @CollectionTable(name = "hospedagem_comodidade", joinColumns = @JoinColumn(name = "hospedagem_id"))
    @Column(name = "comodidade", length = 60)
    private List<String> comodidades = new ArrayList<>();

    @OneToMany(mappedBy = "hospedagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quarto> quartos = new ArrayList<>();

    protected Hospedagem() {
    }

    public Hospedagem(BigDecimal preco, int disponibilidade, Destino destino, ParceiroFornecedor parceiro,
                      String nome, int categoria, String endereco, List<String> comodidades) {
        super(preco, disponibilidade, destino, parceiro);
        this.nome = nome;
        this.categoria = categoria;
        this.endereco = endereco;
        this.comodidades = comodidades == null ? new ArrayList<>() : new ArrayList<>(comodidades);
    }

    public void adicionarQuarto(Quarto quarto) {
        quarto.vincular(this);
        this.quartos.add(quarto);
    }

    public String getNome() {
        return nome;
    }

    public int getCategoria() {
        return categoria;
    }

    public String getEndereco() {
        return endereco;
    }

    public List<String> getComodidades() {
        return comodidades;
    }

    public List<Quarto> getQuartos() {
        return quartos;
    }
}
