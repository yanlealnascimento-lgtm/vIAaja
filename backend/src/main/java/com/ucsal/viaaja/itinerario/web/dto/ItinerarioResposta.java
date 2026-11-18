package com.ucsal.viaaja.itinerario.web.dto;

import com.ucsal.viaaja.itinerario.domain.DiaItinerario;
import com.ucsal.viaaja.itinerario.domain.Itinerario;
import com.ucsal.viaaja.itinerario.domain.ItemItinerario;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ItinerarioResposta(
        Long id,
        String titulo,
        String destino,
        Long destinoId,
        LocalDate dataInicio,
        LocalDate dataFim,
        BigDecimal orcamento,
        BigDecimal custoTotal,
        String origem,
        String status,
        List<String> interesses,
        List<DiaResposta> dias) {

    public record ItemResposta(Long id, String titulo, String descricao, String periodo,
                               LocalTime horaInicio, LocalTime horaFim, BigDecimal custoEstimado, int ordem) {
        static ItemResposta de(ItemItinerario i) {
            return new ItemResposta(i.getId(), i.getTitulo(), i.getDescricao(), i.getPeriodo(),
                    i.getHoraInicio(), i.getHoraFim(), i.getCustoEstimado(), i.getOrdem());
        }
    }

    public record DiaResposta(Long id, LocalDate data, int ordem, BigDecimal custoDoDia,
                              List<ItemResposta> itens) {
        static DiaResposta de(DiaItinerario d) {
            return new DiaResposta(d.getId(), d.getData(), d.getOrdem(), d.custoDoDia(),
                    d.getItens().stream().map(ItemResposta::de).toList());
        }
    }

    public static ItinerarioResposta de(Itinerario i) {
        return new ItinerarioResposta(
                i.getId(), i.getTitulo(),
                i.getDestino() == null ? null : i.getDestino().nomeCompleto(),
                i.getDestino() == null ? null : i.getDestino().getId(),
                i.getDataInicio(), i.getDataFim(), i.getOrcamento(), i.getCustoTotal(),
                i.getOrigem().name(), i.getStatus().name(), i.getInteresses(),
                i.getDias().stream().map(DiaResposta::de).toList());
    }
}
