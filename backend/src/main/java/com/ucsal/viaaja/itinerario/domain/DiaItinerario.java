package com.ucsal.viaaja.itinerario.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "dia_itinerario")
public class DiaItinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private int ordem;

    @ManyToOne
    @JoinColumn(name = "itinerario_id")
    private Itinerario itinerario;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "dia", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("ordem asc")
    private List<ItemItinerario> itens = new ArrayList<>();

    protected DiaItinerario() {
    }

    public DiaItinerario(LocalDate data, int ordem) {
        this.data = data;
        this.ordem = ordem;
    }

    void vincular(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public void adicionarItem(ItemItinerario item) {
        item.vincular(this);
        this.itens.add(item);
    }

    public void limparItens() {
        this.itens.clear();
    }

    public BigDecimal custoDoDia() {
        return itens.stream()
                .map(ItemItinerario::getCustoEstimado)
                .filter(valor -> valor != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public int getOrdem() {
        return ordem;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public List<ItemItinerario> getItens() {
        return itens;
    }
}
