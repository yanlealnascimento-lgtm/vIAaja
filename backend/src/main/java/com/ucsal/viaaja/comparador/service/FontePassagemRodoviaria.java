package com.ucsal.viaaja.comparador.service;

import com.ucsal.viaaja.catalogo.domain.ModalTransporte;
import com.ucsal.viaaja.catalogo.domain.Passagem;
import com.ucsal.viaaja.catalogo.repository.PassagemRepository;
import com.ucsal.viaaja.comparador.domain.OpcaoDeslocamento;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FontePassagemRodoviaria implements FonteDePreco {

    private final PassagemRepository passagemRepository;

    public FontePassagemRodoviaria(PassagemRepository passagemRepository) {
        this.passagemRepository = passagemRepository;
    }

    @Override
    public ModalTransporte modalAtendido() {
        return ModalTransporte.ONIBUS;
    }

    @Override
    public OpcaoDeslocamento cotar(String origem, String destino, LocalDate data, int passageiros) {
        List<Passagem> encontradas = passagemRepository.maisBaratasPorModal(ModalTransporte.ONIBUS,
                origem, destino, data.atStartOfDay(), data.atTime(LocalTime.MAX));
        if (encontradas.isEmpty()) {
            return OpcaoDeslocamento.indisponivel(ModalTransporte.ONIBUS,
                    "Nenhuma viagem rodoviaria encontrada para esta data");
        }
        Passagem maisBarata = encontradas.get(0);
        BigDecimal total = maisBarata.getPreco().multiply(BigDecimal.valueOf(passageiros));
        String detalhe = String.format("%s, %d min de viagem",
                maisBarata.getEmpresa(), maisBarata.duracaoEmMinutos());
        return new OpcaoDeslocamento(ModalTransporte.ONIBUS, total, false, true, detalhe);
    }
}
