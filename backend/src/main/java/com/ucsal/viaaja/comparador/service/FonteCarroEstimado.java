package com.ucsal.viaaja.comparador.service;

import com.ucsal.viaaja.catalogo.domain.Destino;
import com.ucsal.viaaja.catalogo.domain.ModalTransporte;
import com.ucsal.viaaja.catalogo.repository.DestinoRepository;
import com.ucsal.viaaja.comparador.domain.OpcaoDeslocamento;
import com.ucsal.viaaja.config.PropriedadesAplicacao;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class FonteCarroEstimado implements FonteDePreco {

    private final DestinoRepository destinoRepository;
    private final PropriedadesAplicacao propriedades;

    public FonteCarroEstimado(DestinoRepository destinoRepository, PropriedadesAplicacao propriedades) {
        this.destinoRepository = destinoRepository;
        this.propriedades = propriedades;
    }

    @Override
    public ModalTransporte modalAtendido() {
        return ModalTransporte.CARRO;
    }

    @Override
    public OpcaoDeslocamento cotar(String origem, String destino, LocalDate data, int passageiros) {
        Optional<Destino> destinoEncontrado = destinoRepository.findFirstByCidadeIgnoreCase(destino);
        if (destinoEncontrado.isEmpty() || destinoEncontrado.get().getDistanciaReferenciaKm() == null) {
            return OpcaoDeslocamento.indisponivel(ModalTransporte.CARRO,
                    "Distância de referência indisponível para este destino");
        }

        int distancia = destinoEncontrado.get().getDistanciaReferenciaKm();
        var config = propriedades.getComparador();

        BigDecimal litros = BigDecimal.valueOf(distancia)
                .divide(BigDecimal.valueOf(config.getConsumoMedioKmPorLitro()), 2, RoundingMode.HALF_UP);
        BigDecimal combustivel = litros.multiply(BigDecimal.valueOf(config.getPrecoCombustivelPorLitro()));
        BigDecimal pedagio = BigDecimal.valueOf(distancia)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(config.getPedagioPorCemKm()));

        BigDecimal total = combustivel.add(pedagio)
                .multiply(BigDecimal.valueOf(2))
                .setScale(2, RoundingMode.HALF_UP);

        NumberFormat reais = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String detalhe = String.format(
                "Ida e volta, %d km: combustível %s e pedágio %s. "
                        + "Valor por veículo, não muda com o número de passageiros",
                distancia * 2,
                reais.format(combustivel.multiply(BigDecimal.valueOf(2))),
                reais.format(pedagio.multiply(BigDecimal.valueOf(2))));

        return new OpcaoDeslocamento(ModalTransporte.CARRO, total, true, true, detalhe);
    }
}
