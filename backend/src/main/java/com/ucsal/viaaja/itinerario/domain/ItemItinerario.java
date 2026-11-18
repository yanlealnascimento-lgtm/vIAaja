package com.ucsal.viaaja.itinerario.domain;

import com.ucsal.viaaja.catalogo.domain.Atividade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "item_itinerario")
public class ItemItinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 600)
    private String descricao;

    @Column(length = 20)
    private String periodo;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @Column(name = "custo_estimado", precision = 12, scale = 2)
    private BigDecimal custoEstimado;

    @Column(nullable = false)
    private int ordem;

    @ManyToOne
    @JoinColumn(name = "atividade_id")
    private Atividade atividade;

    @ManyToOne
    @JoinColumn(name = "dia_id")
    private DiaItinerario dia;

    protected ItemItinerario() {
    }

    public ItemItinerario(String titulo, String descricao, String periodo, LocalTime horaInicio,
                          LocalTime horaFim, BigDecimal custoEstimado, int ordem) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.periodo = periodo;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.custoEstimado = custoEstimado;
        this.ordem = ordem;
    }

    void vincular(DiaItinerario dia) {
        this.dia = dia;
    }

    public void atualizar(String titulo, String descricao, String periodo, LocalTime horaInicio,
                          LocalTime horaFim, BigDecimal custoEstimado, int ordem) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.periodo = periodo;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.custoEstimado = custoEstimado;
        this.ordem = ordem;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getPeriodo() {
        return periodo;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public BigDecimal getCustoEstimado() {
        return custoEstimado;
    }

    public int getOrdem() {
        return ordem;
    }

    public Atividade getAtividade() {
        return atividade;
    }

    public DiaItinerario getDia() {
        return dia;
    }
}
