package com.ucsal.viaaja.avaliacao.domain;

import com.ucsal.viaaja.catalogo.domain.Destino;
import com.ucsal.viaaja.catalogo.domain.Hospedagem;
import com.ucsal.viaaja.identidade.domain.Viajante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int nota;

    @Column(nullable = false, length = 1000)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    @Column(nullable = false)
    private boolean moderada = false;

    @Column(nullable = false)
    private boolean oculta = false;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private Viajante autor;

    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    private Destino destino;

    @ManyToOne
    @JoinColumn(name = "hospedagem_id")
    private Hospedagem hospedagem;

    protected Avaliacao() {
    }

    public Avaliacao(int nota, String comentario, Viajante autor, Destino destino, Hospedagem hospedagem) {
        this.nota = nota;
        this.comentario = comentario;
        this.autor = autor;
        this.destino = destino;
        this.hospedagem = hospedagem;
    }

    public void ocultar() {
        this.oculta = true;
        this.moderada = true;
    }

    public void liberar() {
        this.oculta = false;
        this.moderada = true;
    }

    public Long getId() {
        return id;
    }

    public int getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDateTime getData() {
        return data;
    }

    public boolean isModerada() {
        return moderada;
    }

    public boolean isOculta() {
        return oculta;
    }

    public Viajante getAutor() {
        return autor;
    }

    public Destino getDestino() {
        return destino;
    }

    public Hospedagem getHospedagem() {
        return hospedagem;
    }
}
