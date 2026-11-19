package com.ucsal.viaaja.social.domain;

import com.ucsal.viaaja.catalogo.domain.Destino;
import com.ucsal.viaaja.identidade.domain.Viajante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "convite")
public class Convite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusConvite status = StatusConvite.ENVIADO;

    @Column(length = 300)
    private String mensagem;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio = LocalDateTime.now();

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    @ManyToOne
    @JoinColumn(name = "remetente_id", nullable = false)
    private Viajante remetente;

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Viajante destinatario;

    @ManyToOne
    @JoinColumn(name = "destino_id")
    private Destino destino;

    protected Convite() {
    }

    public Convite(Viajante remetente, Viajante destinatario, Destino destino, String mensagem) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.destino = destino;
        this.mensagem = mensagem;
    }

    public void aceitar() {
        this.status = StatusConvite.ACEITO;
        this.dataResposta = LocalDateTime.now();
    }

    public void recusar() {
        this.status = StatusConvite.RECUSADO;
        this.dataResposta = LocalDateTime.now();
    }

    public boolean pendente() {
        return status == StatusConvite.ENVIADO;
    }

    public Long getId() {
        return id;
    }

    public StatusConvite getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public LocalDateTime getDataResposta() {
        return dataResposta;
    }

    public Viajante getRemetente() {
        return remetente;
    }

    public Viajante getDestinatario() {
        return destinatario;
    }

    public Destino getDestino() {
        return destino;
    }
}
