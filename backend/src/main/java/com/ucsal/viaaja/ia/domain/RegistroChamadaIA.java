package com.ucsal.viaaja.ia.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_chamada_ia")
public class RegistroChamadaIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String funcionalidade;

    @Column(length = 40)
    private String provedor;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "tempo_resposta_ms", nullable = false)
    private long tempoRespostaMs;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 300)
    private String detalhe;

    @Column(name = "usou_cache", nullable = false)
    private boolean usouCache;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    protected RegistroChamadaIA() {
    }

    public RegistroChamadaIA(String funcionalidade, String provedor, Long usuarioId,
                             long tempoRespostaMs, String status, String detalhe, boolean usouCache) {
        this.funcionalidade = funcionalidade;
        this.provedor = provedor;
        this.usuarioId = usuarioId;
        this.tempoRespostaMs = tempoRespostaMs;
        this.status = status;
        this.detalhe = detalhe;
        this.usouCache = usouCache;
    }

    public Long getId() {
        return id;
    }

    public String getFuncionalidade() {
        return funcionalidade;
    }

    public String getProvedor() {
        return provedor;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public long getTempoRespostaMs() {
        return tempoRespostaMs;
    }

    public String getStatus() {
        return status;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public boolean isUsouCache() {
        return usouCache;
    }

    public LocalDateTime getData() {
        return data;
    }
}
