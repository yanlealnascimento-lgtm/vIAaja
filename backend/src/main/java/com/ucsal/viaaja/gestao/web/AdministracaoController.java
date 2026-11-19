package com.ucsal.viaaja.gestao.web;

import com.ucsal.viaaja.avaliacao.domain.Avaliacao;
import com.ucsal.viaaja.avaliacao.repository.AvaliacaoRepository;
import com.ucsal.viaaja.avaliacao.service.AvaliacaoService;
import com.ucsal.viaaja.common.exception.RecursoNaoEncontradoException;
import com.ucsal.viaaja.ia.domain.RegistroChamadaIA;
import com.ucsal.viaaja.ia.repository.RegistroChamadaIARepository;
import com.ucsal.viaaja.identidade.domain.ParceiroFornecedor;
import com.ucsal.viaaja.identidade.repository.ParceiroRepository;
import com.ucsal.viaaja.identidade.repository.UsuarioRepository;
import com.ucsal.viaaja.identidade.web.dto.UsuarioResposta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Painel administrativo", description = "RF-024 Gestao, moderacao e indicadores de uso")
public class AdministracaoController {

    private final UsuarioRepository usuarioRepository;
    private final ParceiroRepository parceiroRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final AvaliacaoService avaliacaoService;
    private final RegistroChamadaIARepository registroIARepository;

    public AdministracaoController(UsuarioRepository usuarioRepository,
                                   ParceiroRepository parceiroRepository,
                                   AvaliacaoRepository avaliacaoRepository,
                                   AvaliacaoService avaliacaoService,
                                   RegistroChamadaIARepository registroIARepository) {
        this.usuarioRepository = usuarioRepository;
        this.parceiroRepository = parceiroRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.avaliacaoService = avaliacaoService;
        this.registroIARepository = registroIARepository;
    }

    public record ParceiroResposta(Long id, String nome, String razaoSocial, String cnpj,
                                   String tipo, boolean aprovado) {
        static ParceiroResposta de(ParceiroFornecedor p) {
            return new ParceiroResposta(p.getId(), p.getNome(), p.getRazaoSocial(), p.getCnpj(),
                    p.getTipoParceiro().name(), p.isAprovado());
        }
    }

    public record ChamadaIAResposta(Long id, String funcionalidade, String provedor, long tempoRespostaMs,
                                    String status, LocalDateTime data) {
        static ChamadaIAResposta de(RegistroChamadaIA r) {
            return new ChamadaIAResposta(r.getId(), r.getFuncionalidade(), r.getProvedor(),
                    r.getTempoRespostaMs(), r.getStatus(), r.getData());
        }
    }

    @GetMapping("/usuarios")
    @Operation(summary = "RF-024 Lista todos os usuarios da plataforma")
    public List<UsuarioResposta> usuarios() {
        return usuarioRepository.findAllByOrderByNomeAsc().stream().map(UsuarioResposta::de).toList();
    }

    @GetMapping("/parceiros")
    @Operation(summary = "RF-024 Lista parceiros, com filtro por situacao de aprovacao")
    public List<ParceiroResposta> parceiros(@RequestParam(required = false) Boolean aprovado) {
        var lista = aprovado == null ? parceiroRepository.findAll()
                : parceiroRepository.findByAprovado(aprovado);
        return lista.stream().map(ParceiroResposta::de).toList();
    }

    @PatchMapping("/parceiros/{id}/aprovar")
    @Operation(summary = "RF-024 Aprova o cadastro de um parceiro fornecedor")
    @Transactional
    public ParceiroResposta aprovar(@PathVariable Long id) {
        ParceiroFornecedor parceiro = parceiroRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Parceiro", id));
        parceiro.aprovar();
        return ParceiroResposta.de(parceiro);
    }

    @GetMapping("/avaliacoes")
    @Operation(summary = "RF-024 Lista as avaliacoes publicadas para moderacao")
    public List<Map<String, Object>> avaliacoes() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Avaliacao a : avaliacaoRepository.findByOcultaFalseOrderByDataDesc()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", a.getId());
            item.put("nota", a.getNota());
            item.put("comentario", a.getComentario());
            item.put("autor", a.getAutor().getNome());
            item.put("destino", a.getDestino().nomeCompleto());
            item.put("data", a.getData());
            resultado.add(item);
        }
        return resultado;
    }

    @PatchMapping("/avaliacoes/{id}/ocultar")
    @Operation(summary = "RF-024 Oculta uma avaliacao apos moderacao")
    public ResponseEntity<Void> ocultar(@PathVariable Long id) {
        avaliacaoService.moderar(id, true);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/indicadores")
    @Operation(summary = "RF-024 Indicadores de uso, incluindo o volume de chamadas a API de IA")
    public Map<String, Object> indicadores() {
        Map<String, Object> indicadores = new LinkedHashMap<>();
        indicadores.put("totalUsuarios", usuarioRepository.count());
        indicadores.put("totalParceiros", parceiroRepository.count());
        indicadores.put("parceirosPendentes", parceiroRepository.findByAprovado(false).size());
        indicadores.put("totalAvaliacoes", avaliacaoRepository.count());
        indicadores.put("chamadasIaTotais", registroIARepository.count());
        indicadores.put("chamadasIaUltimas24h",
                registroIARepository.countByDataAfter(LocalDateTime.now().minusDays(1)));
        indicadores.put("chamadasIaComFalha", registroIARepository.contarPorStatus("INDISPONIVEL"));

        List<Map<String, Object>> porFuncionalidade = new ArrayList<>();
        for (Object[] linha : registroIARepository.consolidarPorFuncionalidade()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("funcionalidade", linha[0]);
            item.put("chamadas", linha[1]);
            item.put("tempoMedioMs", linha[2] == null ? 0 : Math.round((Double) linha[2]));
            porFuncionalidade.add(item);
        }
        indicadores.put("chamadasPorFuncionalidade", porFuncionalidade);
        return indicadores;
    }

    @GetMapping("/chamadas-ia")
    @Operation(summary = "RF-024 Ultimas chamadas registradas para a API de IA")
    public List<ChamadaIAResposta> chamadasIa() {
        return registroIARepository.findTop50ByOrderByDataDesc().stream()
                .map(ChamadaIAResposta::de).toList();
    }
}
