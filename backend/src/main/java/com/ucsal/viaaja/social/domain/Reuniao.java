package com.ucsal.viaaja.social.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reuniao")
public class Reuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sala_id", nullable = false, unique = true, length = 60)
    private String salaId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(name = "encerrada_em")
    private LocalDateTime encerradaEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusReuniao status = StatusReuniao.AGENDADA;

    @Column(name = "traducao_ativa", nullable = false)
    private boolean traducaoAtiva = true;

    @ElementCollection
    @CollectionTable(name = "reuniao_idioma", joinColumns = @JoinColumn(name = "reuniao_id"))
    @Column(name = "idioma", length = 10)
    private List<String> idiomas = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "convite_id", nullable = false)
    private Convite convite;

    public enum StatusReuniao {
        AGENDADA,
        EM_ANDAMENTO,
        ENCERRADA
    }

    protected Reuniao() {
    }

    public Reuniao(String salaId, Convite convite, List<String> idiomas) {
        this.salaId = salaId;
        this.convite = convite;
        this.idiomas = idiomas == null ? new ArrayList<>() : new ArrayList<>(idiomas);
    }

    public void iniciar() {
        this.status = StatusReuniao.EM_ANDAMENTO;
    }

    public void encerrar() {
        this.status = StatusReuniao.ENCERRADA;
        this.encerradaEm = LocalDateTime.now();
    }

    public boolean participa(Long viajanteId) {
        return convite.getRemetente().getId().equals(viajanteId)
                || convite.getDestinatario().getId().equals(viajanteId);
    }

    public Long getId() {
        return id;
    }

    public String getSalaId() {
        return salaId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public LocalDateTime getEncerradaEm() {
        return encerradaEm;
    }

    public StatusReuniao getStatus() {
        return status;
    }

    public boolean isTraducaoAtiva() {
        return traducaoAtiva;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public Convite getConvite() {
        return convite;
    }
}
