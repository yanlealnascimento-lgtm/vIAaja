package com.ucsal.viaaja.avaliacao.web;

import com.ucsal.viaaja.avaliacao.domain.Avaliacao;
import com.ucsal.viaaja.avaliacao.service.AvaliacaoService;
import com.ucsal.viaaja.ia.dto.ResumoAvaliacoes;
import com.ucsal.viaaja.identidade.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/avaliacoes")
@Tag(name = "Avaliacoes", description = "RF-021 Registro de opinioes e RF-013 Resumo inteligente")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    public record AvaliacaoRequisicao(
            @NotNull Long destinoId,
            Long hospedagemId,
            @Min(1) @Max(5) int nota,
            @NotBlank @Size(max = 1000) String comentario) {
    }

    public record AvaliacaoResposta(Long id, int nota, String comentario, String autor,
                                    String destino, LocalDateTime data) {
        static AvaliacaoResposta de(Avaliacao a) {
            return new AvaliacaoResposta(a.getId(), a.getNota(), a.getComentario(),
                    a.getAutor().getNome(), a.getDestino().nomeCompleto(), a.getData());
        }
    }

    @PostMapping
    @Operation(summary = "RF-021 Registra nota e comentario sobre um destino")
    @PreAuthorize("hasRole('VIAJANTE')")
    public ResponseEntity<AvaliacaoResposta> registrar(@RequestBody @Valid AvaliacaoRequisicao requisicao,
                                                       @AuthenticationPrincipal UsuarioAutenticado usuario) {
        Avaliacao avaliacao = service.registrar(usuario.getId(), requisicao.destinoId(),
                requisicao.hospedagemId(), requisicao.nota(), requisicao.comentario());
        return ResponseEntity.status(HttpStatus.CREATED).body(AvaliacaoResposta.de(avaliacao));
    }

    @GetMapping("/destino/{destinoId}")
    @Operation(summary = "Lista as avaliacoes publicas de um destino")
    public List<AvaliacaoResposta> porDestino(@PathVariable Long destinoId) {
        return service.porDestino(destinoId).stream().map(AvaliacaoResposta::de).toList();
    }

    @GetMapping("/destino/{destinoId}/media")
    @Operation(summary = "Media das notas de um destino")
    public Map<String, Object> media(@PathVariable Long destinoId) {
        Double media = service.mediaDoDestino(destinoId);
        return Map.of("destinoId", destinoId, "media", media == null ? 0.0 : media);
    }

    @GetMapping("/destino/{destinoId}/resumo")
    @Operation(summary = "RF-013 Resumo das avaliacoes gerado por IA")
    public ResumoAvaliacoes resumo(@PathVariable Long destinoId,
                                   @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return service.resumirDestino(destinoId, usuario == null ? null : usuario.getId());
    }
}
