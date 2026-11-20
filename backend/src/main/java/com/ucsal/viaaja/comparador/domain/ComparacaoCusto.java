package com.ucsal.viaaja.comparador.domain;

import com.ucsal.viaaja.catalogo.domain.ModalTransporte;
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
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "comparacao_custo")
public class ComparacaoCusto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solicitante_id")
    private Long solicitanteId;

    @Column(nullable = false, length = 120)
    private String origem;

    @Column(nullable = false, length = 120)
    private String destino;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private int passageiros;

    @Column(name = "consultado_em", nullable = false)
    private LocalDateTime consultadoEm = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "modal_mais_barato", length = 10)
    private ModalTransporte modalMaisBarato;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "comparacao_opcao", joinColumns = @JoinColumn(name = "comparacao_id"))
    private List<OpcaoDeslocamento> opcoes = new ArrayList<>();

    protected ComparacaoCusto() {
    }

    public ComparacaoCusto(Long solicitanteId, String origem, String destino, LocalDate data, int passageiros) {
        this.solicitanteId = solicitanteId;
        this.origem = origem;
        this.destino = destino;
        this.data = data;
        this.passageiros = passageiros;
    }

    public void registrar(List<OpcaoDeslocamento> opcoes) {
        this.opcoes = new ArrayList<>(opcoes);
        this.modalMaisBarato = maisBarata().map(OpcaoDeslocamento::getModal).orElse(null);
    }

    public Optional<OpcaoDeslocamento> maisBarata() {
        return opcoes.stream()
                .filter(OpcaoDeslocamento::isDisponivel)
                .filter(o -> o.getCustoTotal() != null)
                .min(Comparator.comparing(OpcaoDeslocamento::getCustoTotal));
    }

    public Long getId() {
        return id;
    }

    public Long getSolicitanteId() {
        return solicitanteId;
    }

    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public LocalDate getData() {
        return data;
    }

    public int getPassageiros() {
        return passageiros;
    }

    public LocalDateTime getConsultadoEm() {
        return consultadoEm;
    }

    public ModalTransporte getModalMaisBarato() {
        return modalMaisBarato;
    }

    public List<OpcaoDeslocamento> getOpcoes() {
        return opcoes;
    }
}
