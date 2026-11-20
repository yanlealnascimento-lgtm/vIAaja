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
public class FontePassagemAerea implements FonteDePreco {

    private final PassagemRepository passagemRepository;

    public FontePassagemAerea(PassagemRepository passagemRepository) {
        this.passagemRepository = passagemRepository;
    }

    @Override
    public ModalTransporte modalAtendido() {
        return ModalTransporte.AVIAO;
    }

    @Override
    public OpcaoDeslocamento cotar(String origem, String destino, LocalDate data, int passageiros) {
        List<Passagem> encontradas = passagemRepository.maisBaratasPorModal(ModalTransporte.AVIAO,
                origem, destino, data.atStartOfDay(), data.atTime(LocalTime.MAX));
        if (encontradas.isEmpty()) {
            return OpcaoDeslocamento.indisponivel(ModalTransporte.AVIAO,
                    "Nenhum voo encontrado para esta data");
        }
        Passagem maisBarata = encontradas.get(0);
        BigDecimal total = maisBarata.getPreco().multiply(BigDecimal.valueOf(passageiros));
        long minutos = maisBarata.duracaoEmMinutos();
        String paradas = maisBarata.getNumeroParadas() == 0 ? "voo direto"
                : maisBarata.getNumeroParadas() == 1 ? "1 parada" : maisBarata.getNumeroParadas() + " paradas";
        String detalhe = String.format("%s, %s, %dh%02d de voo",
                maisBarata.getEmpresa(), paradas, minutos / 60, minutos % 60);
        return new OpcaoDeslocamento(ModalTransporte.AVIAO, total, false, true, detalhe);
    }
}
