package com.ucsal.viaaja.comparador.service;

import com.ucsal.viaaja.comparador.domain.ComparacaoCusto;
import com.ucsal.viaaja.comparador.domain.OpcaoDeslocamento;
import com.ucsal.viaaja.comparador.repository.ComparacaoCustoRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComparadorService {

    private static final Logger log = LoggerFactory.getLogger(ComparadorService.class);

    private final List<FonteDePreco> fontes;
    private final ComparacaoCustoRepository repository;

    public ComparadorService(List<FonteDePreco> fontes, ComparacaoCustoRepository repository) {
        this.fontes = fontes;
        this.repository = repository;
    }

    @Transactional
    public ComparacaoCusto comparar(Long solicitanteId, String origem, String destino,
                                    LocalDate data, int passageiros) {
        ComparacaoCusto comparacao = new ComparacaoCusto(solicitanteId, origem, destino, data, passageiros);
        List<OpcaoDeslocamento> opcoes = new ArrayList<>();

        for (FonteDePreco fonte : fontes) {
            try {
                opcoes.add(fonte.cotar(origem, destino, data, passageiros));
            } catch (Exception ex) {
                log.warn("Fonte {} indisponível: {}", fonte.modalAtendido(), ex.getMessage());
                opcoes.add(OpcaoDeslocamento.indisponivel(fonte.modalAtendido(),
                        "Fonte temporariamente indisponível, resultado parcial"));
            }
        }

        comparacao.registrar(opcoes);
        return repository.save(comparacao);
    }

    @Transactional(readOnly = true)
    public List<ComparacaoCusto> historico(Long solicitanteId) {
        return repository.findTop10BySolicitanteIdOrderByConsultadoEmDesc(solicitanteId);
    }
}
