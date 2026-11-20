package com.ucsal.viaaja.social.service;

import com.ucsal.viaaja.catalogo.domain.Destino;
import com.ucsal.viaaja.catalogo.repository.DestinoRepository;
import com.ucsal.viaaja.common.exception.RecursoNaoEncontradoException;
import com.ucsal.viaaja.common.exception.RegraDeNegocioException;
import com.ucsal.viaaja.ia.service.IntegracaoIAService;
import com.ucsal.viaaja.identidade.domain.Viajante;
import com.ucsal.viaaja.identidade.repository.ViajanteRepository;
import com.ucsal.viaaja.identidade.service.UsuarioService;
import com.ucsal.viaaja.itinerario.repository.ItinerarioRepository;
import com.ucsal.viaaja.notificacao.domain.Notificacao;
import com.ucsal.viaaja.notificacao.service.NotificacaoService;
import com.ucsal.viaaja.social.domain.Convite;
import com.ucsal.viaaja.social.domain.Reuniao;
import com.ucsal.viaaja.social.domain.StatusConvite;
import com.ucsal.viaaja.social.repository.ConviteRepository;
import com.ucsal.viaaja.social.repository.ReuniaoRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SocialService {

    private static final int LIMITE_CONVITES_POR_DIA = 3;

    private final ConviteRepository conviteRepository;
    private final ReuniaoRepository reuniaoRepository;
    private final ViajanteRepository viajanteRepository;
    private final ItinerarioRepository itinerarioRepository;
    private final DestinoRepository destinoRepository;
    private final UsuarioService usuarioService;
    private final NotificacaoService notificacaoService;
    private final IntegracaoIAService integracaoIA;

    public SocialService(ConviteRepository conviteRepository,
                         ReuniaoRepository reuniaoRepository,
                         ViajanteRepository viajanteRepository,
                         ItinerarioRepository itinerarioRepository,
                         DestinoRepository destinoRepository,
                         UsuarioService usuarioService,
                         NotificacaoService notificacaoService,
                         IntegracaoIAService integracaoIA) {
        this.conviteRepository = conviteRepository;
        this.reuniaoRepository = reuniaoRepository;
        this.viajanteRepository = viajanteRepository;
        this.itinerarioRepository = itinerarioRepository;
        this.destinoRepository = destinoRepository;
        this.usuarioService = usuarioService;
        this.notificacaoService = notificacaoService;
        this.integracaoIA = integracaoIA;
    }

    @Transactional(readOnly = true)
    public List<Viajante> descobrir(Long viajanteId, String cidade) {
        Set<Viajante> encontrados = new LinkedHashSet<>();

        if (cidade != null && !cidade.isBlank()) {
            List<Long> interessados = itinerarioRepository.viajantesComInteresseNoDestino(cidade, viajanteId);
            viajanteRepository.findAllById(interessados).stream()
                    .filter(v -> v.getPreferenciaPrivacidade().isVisivelComoProximo())
                    .forEach(encontrados::add);
        }

        Viajante atual = usuarioService.viajantePorId(viajanteId);
        if (atual.getCidadeAproximada() != null) {
            encontrados.addAll(
                    viajanteRepository.buscarProximosPorCidade(atual.getCidadeAproximada(), viajanteId));
        }

        return new ArrayList<>(encontrados);
    }

    @Transactional
    public Convite enviarConvite(Long remetenteId, Long destinatarioId, Long destinoId, String mensagem) {
        if (remetenteId.equals(destinatarioId)) {
            throw new RegraDeNegocioException("Não é possível enviar um convite para você mesmo");
        }

        Viajante remetente = usuarioService.viajantePorId(remetenteId);
        Viajante destinatario = usuarioService.viajantePorId(destinatarioId);

        Destino destino = destinoId == null ? null
                : destinoRepository.findById(destinoId)
                        .orElseThrow(() -> RecursoNaoEncontradoException.de("Destino", destinoId));

        boolean mesmoDestino = destino != null
                && itinerarioRepository.destinosDoViajante(destinatarioId).contains(destino.getCidade());

        if (!destinatario.getPreferenciaPrivacidade().aceitaConviteDe(mesmoDestino)) {
            throw new RegraDeNegocioException("RN-012",
                    "Este viajante não está aceitando convites no momento");
        }

        long enviadosHoje = conviteRepository.countByRemetenteIdAndDestinatarioIdAndDataEnvioAfter(
                remetenteId, destinatarioId, LocalDateTime.now().minusDays(1));
        if (enviadosHoje >= LIMITE_CONVITES_POR_DIA) {
            throw new RegraDeNegocioException("RN-012",
                    "Limite de " + LIMITE_CONVITES_POR_DIA + " convites por dia para o mesmo viajante");
        }

        Convite convite = conviteRepository.save(new Convite(remetente, destinatario, destino, mensagem));
        notificacaoService.enviar(destinatarioId, Notificacao.TipoNotificacao.CONVITE,
                remetente.getNome() + " quer conversar sobre uma viagem com você");
        return convite;
    }

    @Transactional
    public Reuniao aceitarConvite(Long conviteId, Long destinatarioId, List<String> idiomas) {
        Convite convite = conviteRepository.findById(conviteId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Convite", conviteId));

        if (!convite.getDestinatario().getId().equals(destinatarioId)) {
            throw RecursoNaoEncontradoException.de("Convite", conviteId);
        }
        if (!convite.pendente()) {
            throw new RegraDeNegocioException("Este convite já foi respondido");
        }

        convite.aceitar();
        Reuniao reuniao = reuniaoRepository.save(
                new Reuniao("sala-" + UUID.randomUUID(), convite, idiomas));

        notificacaoService.enviar(convite.getRemetente().getId(), Notificacao.TipoNotificacao.REUNIAO,
                convite.getDestinatario().getNome() + " aceitou seu convite. A sala de vídeo está disponível.");
        return reuniao;
    }

    @Transactional
    public void recusarConvite(Long conviteId, Long destinatarioId) {
        Convite convite = conviteRepository.findById(conviteId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Convite", conviteId));
        if (!convite.getDestinatario().getId().equals(destinatarioId)) {
            throw RecursoNaoEncontradoException.de("Convite", conviteId);
        }
        if (!convite.pendente()) {
            throw new RegraDeNegocioException("Este convite já foi respondido");
        }
        convite.recusar();
    }

    @Transactional(readOnly = true)
    public List<Convite> convitesRecebidos(Long viajanteId) {
        return conviteRepository.findByDestinatarioIdOrderByDataEnvioDesc(viajanteId);
    }

    @Transactional(readOnly = true)
    public List<Convite> convitesEnviados(Long viajanteId) {
        return conviteRepository.findByRemetenteIdOrderByDataEnvioDesc(viajanteId);
    }

    @Transactional(readOnly = true)
    public List<Reuniao> reunioes(Long viajanteId) {
        return reuniaoRepository.doViajante(viajanteId);
    }

    @Transactional
    public Reuniao entrarNaSala(String salaId, Long viajanteId) {
        Reuniao reuniao = reuniaoRepository.findBySalaId(salaId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Reunião", salaId));
        if (!reuniao.participa(viajanteId)) {
            throw RecursoNaoEncontradoException.de("Reunião", salaId);
        }
        if (reuniao.getStatus() == Reuniao.StatusReuniao.ENCERRADA) {
            throw new RegraDeNegocioException("Esta conversa já foi encerrada");
        }
        reuniao.iniciar();
        return reuniao;
    }

    @Transactional
    public void encerrarSala(String salaId, Long viajanteId) {
        Reuniao reuniao = reuniaoRepository.findBySalaId(salaId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Reunião", salaId));
        if (!reuniao.participa(viajanteId)) {
            throw RecursoNaoEncontradoException.de("Reunião", salaId);
        }
        reuniao.encerrar();
    }

    @Transactional(readOnly = true)
    public String traduzirFala(String salaId, Long viajanteId, String fala, String idiomaDestino) {
        Reuniao reuniao = reuniaoRepository.findBySalaId(salaId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Reunião", salaId));
        if (!reuniao.participa(viajanteId)) {
            throw RecursoNaoEncontradoException.de("Reunião", salaId);
        }
        return integracaoIA.traduzir(fala, idiomaDestino, viajanteId);
    }

    @Transactional(readOnly = true)
    public boolean existeConviteAceito(Long remetenteId, Long destinatarioId) {
        return conviteRepository.existsByRemetenteIdAndDestinatarioIdAndStatus(
                remetenteId, destinatarioId, StatusConvite.ACEITO);
    }
}
