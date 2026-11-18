package com.ucsal.viaaja.itinerario.web;

import com.ucsal.viaaja.ia.dto.RoteiroSugerido;
import com.ucsal.viaaja.identidade.security.UsuarioAutenticado;
import com.ucsal.viaaja.itinerario.service.ItinerarioService;
import com.ucsal.viaaja.itinerario.web.dto.ItinerarioResposta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/itinerarios")
@PreAuthorize("hasRole('VIAJANTE')")
@Tag(name = "Itinerarios", description = "RF-010 e RF-011 Montagem do roteiro e geracao automatica com IA")
public class ItinerarioController {

    private final ItinerarioService service;

    public ItinerarioController(ItinerarioService service) {
        this.service = service;
    }

    public record CriacaoRequisicao(
            @NotBlank String titulo,
            @NotNull Long destinoId,
            @NotNull LocalDate dataInicio,
            @NotNull LocalDate dataFim,
            BigDecimal orcamento,
            List<String> interesses) {
    }

    public record SugestaoRequisicao(
            @NotNull Long destinoId,
            @NotNull LocalDate dataInicio,
            @NotNull LocalDate dataFim,
            BigDecimal orcamento,
            List<String> interesses) {
    }

    public record AceiteRequisicao(
            @NotBlank String titulo,
            @NotNull Long destinoId,
            @NotNull LocalDate dataInicio,
            @NotNull LocalDate dataFim,
            BigDecimal orcamento,
            List<String> interesses,
            @NotNull RoteiroSugerido roteiro) {
    }

    public record ItemRequisicao(
            @NotNull Integer ordemDoDia,
            @NotBlank String titulo,
            String descricao,
            String periodo,
            LocalTime horaInicio,
            LocalTime horaFim,
            BigDecimal custoEstimado) {
    }

    @GetMapping
    @Operation(summary = "Lista os itinerarios do viajante")
    public List<ItinerarioResposta> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return service.listar(usuario.getId()).stream().map(ItinerarioResposta::de).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha um itinerario com os dias e itens")
    public ItinerarioResposta detalhar(@PathVariable Long id,
                                       @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ItinerarioResposta.de(service.detalhar(id, usuario.getId()));
    }

    @PostMapping
    @Operation(summary = "RF-011 Cria um itinerario em branco para montagem manual")
    public ResponseEntity<ItinerarioResposta> criar(@RequestBody @Valid CriacaoRequisicao requisicao,
                                                    @AuthenticationPrincipal UsuarioAutenticado usuario) {
        var itinerario = service.criarManual(usuario.getId(), requisicao.titulo(), requisicao.destinoId(),
                requisicao.dataInicio(), requisicao.dataFim(), requisicao.orcamento(), requisicao.interesses());
        return ResponseEntity.status(HttpStatus.CREATED).body(ItinerarioResposta.de(itinerario));
    }

    @PostMapping("/sugestoes")
    @Operation(summary = "RF-010 Gera uma sugestao de roteiro dia a dia pela IA, sem salvar")
    public RoteiroSugerido sugerir(@RequestBody @Valid SugestaoRequisicao requisicao,
                                   @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return service.sugerirComIA(usuario.getId(), requisicao.destinoId(), requisicao.dataInicio(),
                requisicao.dataFim(), requisicao.orcamento(), requisicao.interesses());
    }

    @PostMapping("/sugestoes/aceitar")
    @Operation(summary = "RF-010 Salva a sugestao revisada pelo viajante")
    public ResponseEntity<ItinerarioResposta> aceitar(@RequestBody @Valid AceiteRequisicao requisicao,
                                                      @AuthenticationPrincipal UsuarioAutenticado usuario) {
        var itinerario = service.salvarSugestao(usuario.getId(), requisicao.destinoId(), requisicao.titulo(),
                requisicao.dataInicio(), requisicao.dataFim(), requisicao.orcamento(),
                requisicao.interesses(), requisicao.roteiro());
        return ResponseEntity.status(HttpStatus.CREATED).body(ItinerarioResposta.de(itinerario));
    }

    @PostMapping("/{id}/itens")
    @Operation(summary = "RF-011 Adiciona uma atividade a um dia do itinerario")
    public ItinerarioResposta adicionarItem(@PathVariable Long id,
                                            @RequestBody @Valid ItemRequisicao requisicao,
                                            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        service.adicionarItem(id, usuario.getId(), requisicao.ordemDoDia(), requisicao.titulo(),
                requisicao.descricao(), requisicao.periodo(), requisicao.horaInicio(),
                requisicao.horaFim(), requisicao.custoEstimado());
        return ItinerarioResposta.de(service.detalhar(id, usuario.getId()));
    }

    @DeleteMapping("/{id}/itens/{itemId}")
    @Operation(summary = "RF-011 Remove uma atividade do itinerario")
    public ItinerarioResposta removerItem(@PathVariable Long id, @PathVariable Long itemId,
                                          @AuthenticationPrincipal UsuarioAutenticado usuario) {
        service.removerItem(id, usuario.getId(), itemId);
        return ItinerarioResposta.de(service.detalhar(id, usuario.getId()));
    }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirma o itinerario apos a revisao do viajante")
    public ItinerarioResposta confirmar(@PathVariable Long id,
                                        @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ItinerarioResposta.de(service.confirmar(id, usuario.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um itinerario")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @AuthenticationPrincipal UsuarioAutenticado usuario) {
        service.excluir(id, usuario.getId());
        return ResponseEntity.noContent().build();
    }
}
