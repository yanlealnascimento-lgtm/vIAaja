package com.ucsal.viaaja.ia.service;

import com.ucsal.viaaja.common.exception.ServicoIndisponivelException;
import com.ucsal.viaaja.ia.ServicoIA;
import com.ucsal.viaaja.ia.domain.RegistroChamadaIA;
import com.ucsal.viaaja.ia.dto.DadosGeracaoRoteiro;
import com.ucsal.viaaja.ia.dto.ParametrosBusca;
import com.ucsal.viaaja.ia.dto.ResumoAvaliacoes;
import com.ucsal.viaaja.ia.dto.RoteiroSugerido;
import com.ucsal.viaaja.ia.repository.RegistroChamadaIARepository;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class IntegracaoIAService {

    private final ServicoIA servicoIA;
    private final RegistroChamadaIARepository registroRepository;

    public IntegracaoIAService(ServicoIA servicoIA, RegistroChamadaIARepository registroRepository) {
        this.servicoIA = servicoIA;
        this.registroRepository = registroRepository;
    }

    public String provedorAtivo() {
        return servicoIA.nome();
    }

    public ParametrosBusca interpretarPedido(String textoLivre, Long usuarioId) {
        return executar("assistente-linguagem-natural", usuarioId,
                () -> servicoIA.interpretarPedido(textoLivre));
    }

    @Cacheable(cacheNames = "roteiros", key = "#dados.hashCode()", unless = "#result == null")
    public RoteiroSugerido gerarItinerario(DadosGeracaoRoteiro dados, Long usuarioId) {
        return executar("geracao-itinerario", usuarioId, () -> servicoIA.gerarItinerario(dados));
    }

    @Cacheable(cacheNames = "resumos", key = "#assunto + '-' + #comentarios.size()")
    public ResumoAvaliacoes resumirAvaliacoes(String assunto, List<String> comentarios, Long usuarioId) {
        return executar("resumo-avaliacoes", usuarioId,
                () -> servicoIA.resumirAvaliacoes(assunto, comentarios));
    }

    public List<String> recomendarDestinos(List<String> historico, List<String> interesses, Long usuarioId) {
        return executar("recomendacoes", usuarioId,
                () -> servicoIA.recomendarDestinos(historico, interesses));
    }

    public String traduzir(String fala, String idiomaDestino, Long usuarioId) {
        return executar("traducao-simultanea", usuarioId, () -> servicoIA.traduzir(fala, idiomaDestino));
    }

    private <T> T executar(String funcionalidade, Long usuarioId, Supplier<T> operacao) {
        long inicio = System.currentTimeMillis();
        try {
            T resultado = operacao.get();
            registrar(funcionalidade, usuarioId, System.currentTimeMillis() - inicio, "SUCESSO", null);
            return resultado;
        } catch (ServicoIndisponivelException ex) {
            registrar(funcionalidade, usuarioId, System.currentTimeMillis() - inicio, "INDISPONIVEL",
                    ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            registrar(funcionalidade, usuarioId, System.currentTimeMillis() - inicio, "ERRO", ex.getMessage());
            throw new ServicoIndisponivelException("IA",
                    "Os recursos inteligentes estao temporariamente fora do ar (RNF-011)");
        }
    }

    private void registrar(String funcionalidade, Long usuarioId, long tempo, String status, String detalhe) {
        registroRepository.save(new RegistroChamadaIA(funcionalidade, servicoIA.nome(), usuarioId,
                tempo, status, detalhe, false));
    }
}
