package com.ucsal.viaaja.catalogo.domain;

import com.ucsal.viaaja.identidade.domain.ParceiroFornecedor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "passagem")
@PrimaryKeyJoinColumn(name = "oferta_id")
public class Passagem extends Oferta {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ModalTransporte modal;

    @Column(nullable = false, length = 120)
    private String origem;

    @Column(name = "nome_destino", nullable = false, length = 120)
    private String nomeDestino;

    @Column(name = "data_partida", nullable = false)
    private LocalDateTime dataPartida;

    @Column(name = "data_chegada", nullable = false)
    private LocalDateTime dataChegada;

    @Column(name = "numero_paradas", nullable = false)
    private int numeroParadas;

    @Column(name = "assentos_disponiveis", nullable = false)
    private int assentosDisponiveis;

    @Column(length = 120)
    private String empresa;

    protected Passagem() {
    }

    public Passagem(BigDecimal preco, int disponibilidade, Destino destino, ParceiroFornecedor parceiro,
                    ModalTransporte modal, String origem, String nomeDestino,
                    LocalDateTime dataPartida, LocalDateTime dataChegada,
                    int numeroParadas, int assentosDisponiveis, String empresa) {
        super(preco, disponibilidade, destino, parceiro);
        this.modal = modal;
        this.origem = origem;
        this.nomeDestino = nomeDestino;
        this.dataPartida = dataPartida;
        this.dataChegada = dataChegada;
        this.numeroParadas = numeroParadas;
        this.assentosDisponiveis = assentosDisponiveis;
        this.empresa = empresa;
    }

    public long duracaoEmMinutos() {
        return Duration.between(dataPartida, dataChegada).toMinutes();
    }

    public ModalTransporte getModal() {
        return modal;
    }

    public String getOrigem() {
        return origem;
    }

    public String getNomeDestino() {
        return nomeDestino;
    }

    public LocalDateTime getDataPartida() {
        return dataPartida;
    }

    public LocalDateTime getDataChegada() {
        return dataChegada;
    }

    public int getNumeroParadas() {
        return numeroParadas;
    }

    public int getAssentosDisponiveis() {
        return assentosDisponiveis;
    }

    public String getEmpresa() {
        return empresa;
    }
}
