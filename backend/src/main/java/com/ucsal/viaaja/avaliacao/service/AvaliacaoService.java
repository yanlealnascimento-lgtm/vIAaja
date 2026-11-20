package com.ucsal.viaaja.avaliacao.service;

import com.ucsal.viaaja.avaliacao.domain.Avaliacao;
import com.ucsal.viaaja.avaliacao.repository.AvaliacaoRepository;
import com.ucsal.viaaja.catalogo.domain.Destino;
import com.ucsal.viaaja.catalogo.domain.Hospedagem;
import com.ucsal.viaaja.catalogo.repository.DestinoRepository;
import com.ucsal.viaaja.catalogo.repository.HospedagemRepository;
import com.ucsal.viaaja.common.exception.RecursoNaoEncontradoException;
import com.ucsal.viaaja.common.exception.RegraDeNegocioException;
import com.ucsal.viaaja.ia.dto.ResumoAvaliacoes;
import com.ucsal.viaaja.ia.service.IntegracaoIAService;
import com.ucsal.viaaja.identidade.service.UsuarioService;
import com.ucsal.viaaja.itinerario.repository.ItinerarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final DestinoRepository destinoRepository;
    private final HospedagemRepository hospedagemRepository;
    private final ItinerarioRepository itinerarioRepository;
    private final UsuarioService usuarioService;
    private final IntegracaoIAService integracaoIA;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            DestinoRepository destinoRepository,
                            HospedagemRepository hospedagemRepository,
                            ItinerarioRepository itinerarioRepository,
                            UsuarioService usuarioService,
                            IntegracaoIAService integracaoIA) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.destinoRepository = destinoRepository;
        this.hospedagemRepository = hospedagemRepository;
        this.itinerarioRepository = itinerarioRepository;
        this.usuarioService = usuarioService;
        this.integracaoIA = integracaoIA;
    }

    @Transactional
    public Avaliacao registrar(Long autorId, Long destinoId, Long hospedagemId, int nota, String comentario) {
        if (nota < 1 || nota > 5) {
            throw new RegraDeNegocioException("A nota deve estar entre 1 e 5");
        }

        Destino destino = destinoRepository.findById(destinoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Destino", destinoId));

        List<String> destinosVisitados = itinerarioRepository.destinosDoViajante(autorId);
        if (!destinosVisitados.contains(destino.getCidade())) {
            throw new RegraDeNegocioException("RN-013",
                    "Só quem tem um itinerário para este destino pode avaliá-lo");
        }

        if (avaliacaoRepository.existsByAutorIdAndDestinoId(autorId, destinoId)) {
            throw new RegraDeNegocioException("Você já avaliou este destino");
        }

        Hospedagem hospedagem = hospedagemId == null ? null
                : hospedagemRepository.findById(hospedagemId)
                        .orElseThrow(() -> RecursoNaoEncontradoException.de("Hospedagem", hospedagemId));

        return avaliacaoRepository.save(new Avaliacao(nota, comentario,
                usuarioService.viajantePorId(autorId), destino, hospedagem));
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> porDestino(Long destinoId) {
        return avaliacaoRepository.findByDestinoIdAndOcultaFalseOrderByDataDesc(destinoId);
    }

    @Transactional(readOnly = true)
    public Double mediaDoDestino(Long destinoId) {
        return avaliacaoRepository.mediaDoDestino(destinoId);
    }

    @Transactional(readOnly = true)
    public ResumoAvaliacoes resumirDestino(Long destinoId, Long usuarioId) {
        Destino destino = destinoRepository.findById(destinoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Destino", destinoId));
        List<String> comentarios = porDestino(destinoId).stream().map(Avaliacao::getComentario).toList();
        if (comentarios.isEmpty()) {
            throw new RegraDeNegocioException("Este destino ainda não tem avaliações para resumir");
        }
        return integracaoIA.resumirAvaliacoes(destino.getCidade(), comentarios, usuarioId);
    }

    @Transactional
    public void moderar(Long avaliacaoId, boolean ocultar) {
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Avaliação", avaliacaoId));
        if (ocultar) {
            avaliacao.ocultar();
        } else {
            avaliacao.liberar();
        }
    }
}
