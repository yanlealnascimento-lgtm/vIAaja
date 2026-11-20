package com.ucsal.viaaja.itinerario.domain;

import com.ucsal.viaaja.catalogo.domain.Destino;
import com.ucsal.viaaja.identidade.domain.Viajante;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "itinerario")
public class Itinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(precision = 12, scale = 2)
    private BigDecimal orcamento;

    @Column(name = "custo_total", precision = 12, scale = 2)
    private BigDecimal custoTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrigemItinerario origem = OrigemItinerario.MANUAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusItinerario status = StatusItinerario.RASCUNHO;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "viajante_id", nullable = false)
    private Viajante viajante;

    @ManyToOne
    @JoinColumn(name = "destino_id")
    private Destino destino;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "itinerario_interesse", joinColumns = @JoinColumn(name = "itinerario_id"))
    @Column(name = "interesse", length = 60)
    private List<String> interesses = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "itinerario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("ordem asc")
    private List<DiaItinerario> dias = new ArrayList<>();

    public enum OrigemItinerario {
        MANUAL,
        SUGERIDO_IA,
        EDITADO_APOS_IA
    }

    public enum StatusItinerario {
        RASCUNHO,
        CONFIRMADO
    }

    protected Itinerario() {
    }

    public Itinerario(String titulo, LocalDate dataInicio, LocalDate dataFim, BigDecimal orcamento,
                      Viajante viajante, Destino destino, List<String> interesses) {
        this.titulo = titulo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.orcamento = orcamento;
        this.viajante = viajante;
        this.destino = destino;
        this.interesses = interesses == null ? new ArrayList<>() : new ArrayList<>(interesses);
    }

    public void adicionarDia(DiaItinerario dia) {
        dia.vincular(this);
        this.dias.add(dia);
    }

    public void substituirDias(List<DiaItinerario> novosDias) {
        this.dias.clear();
        novosDias.forEach(this::adicionarDia);
    }

    public BigDecimal recalcularCustoTotal() {
        this.custoTotal = dias.stream()
                .map(DiaItinerario::custoDoDia)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this.custoTotal;
    }

    public void marcarComoSugeridoPorIA() {
        this.origem = OrigemItinerario.SUGERIDO_IA;
    }

    public void marcarComoEditado() {
        if (this.origem == OrigemItinerario.SUGERIDO_IA) {
            this.origem = OrigemItinerario.EDITADO_APOS_IA;
        }
    }

    public void confirmar() {
        this.status = StatusItinerario.CONFIRMADO;
    }

    public void renomear(String titulo) {
        this.titulo = titulo;
    }

    public boolean pertenceA(Long viajanteId) {
        return viajante != null && viajante.getId().equals(viajanteId);
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public BigDecimal getOrcamento() {
        return orcamento;
    }

    public BigDecimal getCustoTotal() {
        return custoTotal;
    }

    public OrigemItinerario getOrigem() {
        return origem;
    }

    public StatusItinerario getStatus() {
        return status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public Viajante getViajante() {
        return viajante;
    }

    public Destino getDestino() {
        return destino;
    }

    public List<String> getInteresses() {
        return interesses;
    }

    public List<DiaItinerario> getDias() {
        return dias;
    }
}
