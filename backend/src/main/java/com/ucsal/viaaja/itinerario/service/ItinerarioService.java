package com.ucsal.viaaja.itinerario.service;

import com.ucsal.viaaja.catalogo.domain.Atividade;
import com.ucsal.viaaja.catalogo.domain.Destino;
import com.ucsal.viaaja.catalogo.repository.AtividadeRepository;
import com.ucsal.viaaja.catalogo.repository.DestinoRepository;
import com.ucsal.viaaja.common.exception.RecursoNaoEncontradoException;
import com.ucsal.viaaja.common.exception.RegraDeNegocioException;
import com.ucsal.viaaja.ia.dto.DadosGeracaoRoteiro;
import com.ucsal.viaaja.ia.dto.RestricaoHorario;
import com.ucsal.viaaja.ia.dto.RoteiroSugerido;
import com.ucsal.viaaja.ia.service.IntegracaoIAService;
import com.ucsal.viaaja.identidade.domain.Viajante;
import com.ucsal.viaaja.identidade.service.UsuarioService;
import com.ucsal.viaaja.itinerario.domain.DiaItinerario;
import com.ucsal.viaaja.itinerario.domain.Itinerario;
import com.ucsal.viaaja.itinerario.domain.ItemItinerario;
import com.ucsal.viaaja.itinerario.repository.ItinerarioRepository;
import com.ucsal.viaaja.notificacao.domain.Notificacao;
import com.ucsal.viaaja.notificacao.service.NotificacaoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItinerarioService {

    private static final int DIAS_MAXIMOS_PARA_IA = 10;

    private final ItinerarioRepository itinerarioRepository;
    private final DestinoRepository destinoRepository;
    private final AtividadeRepository atividadeRepository;
    private final UsuarioService usuarioService;
    private final IntegracaoIAService integracaoIA;
    private final NotificacaoService notificacaoService;

    public ItinerarioService(ItinerarioRepository itinerarioRepository,
                             DestinoRepository destinoRepository,
                             AtividadeRepository atividadeRepository,
                             UsuarioService usuarioService,
                             IntegracaoIAService integracaoIA,
                             NotificacaoService notificacaoService) {
        this.itinerarioRepository = itinerarioRepository;
        this.destinoRepository = destinoRepository;
        this.atividadeRepository = atividadeRepository;
        this.usuarioService = usuarioService;
        this.integracaoIA = integracaoIA;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public Itinerario criarManual(Long viajanteId, String titulo, Long destinoId, LocalDate inicio,
                                  LocalDate fim, BigDecimal orcamento, List<String> interesses) {
        validarPeriodo(inicio, fim);
        Viajante viajante = usuarioService.viajantePorId(viajanteId);
        Destino destino = destinoRepository.findById(destinoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Destino", destinoId));

        Itinerario itinerario = new Itinerario(titulo, inicio, fim, orcamento, viajante, destino, interesses);
        long totalDias = ChronoUnit.DAYS.between(inicio, fim) + 1;
        for (int i = 0; i < totalDias; i++) {
            itinerario.adicionarDia(new DiaItinerario(inicio.plusDays(i), i + 1));
        }
        itinerario.recalcularCustoTotal();
        return itinerarioRepository.save(itinerario);
    }

    @Transactional
    public RoteiroSugerido sugerirComIA(Long viajanteId, Long destinoId, LocalDate inicio, LocalDate fim,
                                        BigDecimal orcamento, List<String> interesses) {
        validarPeriodo(inicio, fim);
        if (ChronoUnit.DAYS.between(inicio, fim) + 1 > DIAS_MAXIMOS_PARA_IA) {
            throw new RegraDeNegocioException(
                    "A geracao automatica aceita no maximo " + DIAS_MAXIMOS_PARA_IA + " dias");
        }

        Destino destino = destinoRepository.findById(destinoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Destino", destinoId));

        List<String> atividadesDisponiveis = atividadeRepository.findByDestinoId(destinoId).stream()
                .map(Atividade::getNome)
                .toList();

        DadosGeracaoRoteiro dados = new DadosGeracaoRoteiro(destino.getCidade(), inicio, fim,
                orcamento, interesses, List.<RestricaoHorario>of(), atividadesDisponiveis);

        return integracaoIA.gerarItinerario(dados, viajanteId);
    }

    @Transactional
    public Itinerario salvarSugestao(Long viajanteId, Long destinoId, String titulo, LocalDate inicio,
                                     LocalDate fim, BigDecimal orcamento, List<String> interesses,
                                     RoteiroSugerido roteiro) {
        Itinerario itinerario = criarManual(viajanteId, titulo, destinoId, inicio, fim, orcamento, interesses);
        itinerario.marcarComoSugeridoPorIA();

        List<DiaItinerario> dias = new ArrayList<>();
        int ordemDia = 1;
        for (var diaSugerido : roteiro.dias()) {
            DiaItinerario dia = new DiaItinerario(
                    diaSugerido.data() != null ? diaSugerido.data() : inicio.plusDays(ordemDia - 1L),
                    ordemDia);
            int ordemItem = 1;
            for (var atividade : diaSugerido.atividades()) {
                dia.adicionarItem(new ItemItinerario(atividade.titulo(), atividade.descricao(),
                        atividade.periodo(), atividade.horaInicio(), atividade.horaFim(),
                        atividade.custoEstimado(), ordemItem++));
            }
            dias.add(dia);
            ordemDia++;
        }

        itinerario.substituirDias(dias);
        itinerario.recalcularCustoTotal();

        notificacaoService.enviar(viajanteId, Notificacao.TipoNotificacao.ITINERARIO,
                "Roteiro sugerido pela IA foi salvo em " + itinerario.getTitulo());
        return itinerario;
    }

    @Transactional(readOnly = true)
    public List<Itinerario> listar(Long viajanteId) {
        return itinerarioRepository.findByViajanteIdOrderByDataInicioDesc(viajanteId);
    }

    @Transactional(readOnly = true)
    public Itinerario detalhar(Long id, Long viajanteId) {
        Itinerario itinerario = itinerarioRepository.buscarComDias(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Itinerario", id));
        if (!itinerario.pertenceA(viajanteId)) {
            throw RecursoNaoEncontradoException.de("Itinerario", id);
        }
        return itinerario;
    }

    @Transactional
    public ItemItinerario adicionarItem(Long itinerarioId, Long viajanteId, int ordemDoDia, String titulo,
                                        String descricao, String periodo, LocalTime horaInicio,
                                        LocalTime horaFim, BigDecimal custoEstimado) {
        Itinerario itinerario = detalhar(itinerarioId, viajanteId);
        DiaItinerario dia = itinerario.getDias().stream()
                .filter(d -> d.getOrdem() == ordemDoDia)
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Dia " + ordemDoDia + " nao existe no itinerario"));

        int proximaOrdem = dia.getItens().size() + 1;
        ItemItinerario item = new ItemItinerario(titulo, descricao, periodo, horaInicio, horaFim,
                custoEstimado, proximaOrdem);
        dia.adicionarItem(item);
        itinerario.marcarComoEditado();
        itinerario.recalcularCustoTotal();
        return item;
    }

    @Transactional
    public void removerItem(Long itinerarioId, Long viajanteId, Long itemId) {
        Itinerario itinerario = detalhar(itinerarioId, viajanteId);
        boolean removido = itinerario.getDias().stream()
                .anyMatch(dia -> dia.getItens().removeIf(item -> item.getId().equals(itemId)));
        if (!removido) {
            throw RecursoNaoEncontradoException.de("Item do itinerario", itemId);
        }
        itinerario.marcarComoEditado();
        itinerario.recalcularCustoTotal();
    }

    @Transactional
    public Itinerario confirmar(Long itinerarioId, Long viajanteId) {
        Itinerario itinerario = detalhar(itinerarioId, viajanteId);
        itinerario.recalcularCustoTotal();
        itinerario.confirmar();
        notificacaoService.enviar(viajanteId, Notificacao.TipoNotificacao.ITINERARIO,
                "Itinerario " + itinerario.getTitulo() + " confirmado");
        return itinerario;
    }

    @Transactional
    public void excluir(Long itinerarioId, Long viajanteId) {
        Itinerario itinerario = detalhar(itinerarioId, viajanteId);
        itinerarioRepository.delete(itinerario);
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new RegraDeNegocioException("A data final deve ser igual ou posterior a data inicial");
        }
    }
}
