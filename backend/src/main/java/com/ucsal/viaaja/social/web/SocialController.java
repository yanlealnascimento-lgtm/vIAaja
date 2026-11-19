package com.ucsal.viaaja.social.web;

import com.ucsal.viaaja.identidade.domain.Viajante;
import com.ucsal.viaaja.identidade.security.UsuarioAutenticado;
import com.ucsal.viaaja.social.domain.Convite;
import com.ucsal.viaaja.social.domain.Reuniao;
import com.ucsal.viaaja.social.service.SocialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/social")
@PreAuthorize("hasRole('VIAJANTE')")
@Tag(name = "Social", description = "RF-017 a RF-020 Descoberta, convites, reunioes e traducao")
public class SocialController {

    private final SocialService service;

    public SocialController(SocialService service) {
        this.service = service;
    }

    public record ViajanteResposta(Long id, String nome, String cidadeAproximada) {
        static ViajanteResposta de(Viajante v) {
            return new ViajanteResposta(v.getId(), v.getNome(), v.getCidadeAproximada());
        }
    }

    public record ConviteRequisicao(
            @NotNull Long destinatarioId,
            Long destinoId,
            @Size(max = 300) String mensagem) {
    }

    public record ConviteResposta(Long id, String status, String mensagem, String remetente,
                                  String destinatario, String destino, LocalDateTime dataEnvio) {
        static ConviteResposta de(Convite c) {
            return new ConviteResposta(c.getId(), c.getStatus().name(), c.getMensagem(),
                    c.getRemetente().getNome(), c.getDestinatario().getNome(),
                    c.getDestino() == null ? null : c.getDestino().nomeCompleto(), c.getDataEnvio());
        }
    }

    public record AceiteRequisicao(List<String> idiomas) {
    }

    public record ReuniaoResposta(Long id, String salaId, String status, boolean traducaoAtiva,
                                  List<String> idiomas, LocalDateTime dataHora, String comQuem) {
        static ReuniaoResposta de(Reuniao r, Long viajanteId) {
            String outro = r.getConvite().getRemetente().getId().equals(viajanteId)
                    ? r.getConvite().getDestinatario().getNome()
                    : r.getConvite().getRemetente().getNome();
            return new ReuniaoResposta(r.getId(), r.getSalaId(), r.getStatus().name(), r.isTraducaoAtiva(),
                    r.getIdiomas(), r.getDataHora(), outro);
        }
    }

    public record TraducaoRequisicao(@NotBlank String fala, @NotBlank String idiomaDestino) {
    }

    @GetMapping("/viajantes")
    @Operation(summary = "RF-017 Descobre viajantes do mesmo destino ou proximos")
    public List<ViajanteResposta> descobrir(@RequestParam(required = false) String cidade,
                                            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return service.descobrir(usuario.getId(), cidade).stream().map(ViajanteResposta::de).toList();
    }

    @PostMapping("/convites")
    @Operation(summary = "RF-018 Envia um convite para reuniao por video")
    public ResponseEntity<ConviteResposta> convidar(@RequestBody @Valid ConviteRequisicao requisicao,
                                                    @AuthenticationPrincipal UsuarioAutenticado usuario) {
        Convite convite = service.enviarConvite(usuario.getId(), requisicao.destinatarioId(),
                requisicao.destinoId(), requisicao.mensagem());
        return ResponseEntity.status(HttpStatus.CREATED).body(ConviteResposta.de(convite));
    }

    @GetMapping("/convites/recebidos")
    @Operation(summary = "Convites recebidos pelo viajante")
    public List<ConviteResposta> recebidos(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return service.convitesRecebidos(usuario.getId()).stream().map(ConviteResposta::de).toList();
    }

    @GetMapping("/convites/enviados")
    @Operation(summary = "Convites enviados pelo viajante")
    public List<ConviteResposta> enviados(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return service.convitesEnviados(usuario.getId()).stream().map(ConviteResposta::de).toList();
    }

    @PostMapping("/convites/{id}/aceitar")
    @Operation(summary = "RF-019 Aceita o convite e cria a sala de reuniao")
    public ReuniaoResposta aceitar(@PathVariable Long id, @RequestBody AceiteRequisicao requisicao,
                                   @AuthenticationPrincipal UsuarioAutenticado usuario) {
        Reuniao reuniao = service.aceitarConvite(id, usuario.getId(),
                requisicao == null ? List.of("pt-BR") : requisicao.idiomas());
        return ReuniaoResposta.de(reuniao, usuario.getId());
    }

    @PatchMapping("/convites/{id}/recusar")
    @Operation(summary = "Recusa o convite recebido")
    public ResponseEntity<Void> recusar(@PathVariable Long id,
                                        @AuthenticationPrincipal UsuarioAutenticado usuario) {
        service.recusarConvite(id, usuario.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reunioes")
    @Operation(summary = "Lista as reunioes do viajante")
    public List<ReuniaoResposta> reunioes(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return service.reunioes(usuario.getId()).stream()
                .map(r -> ReuniaoResposta.de(r, usuario.getId())).toList();
    }

    @PostMapping("/reunioes/{salaId}/entrar")
    @Operation(summary = "RF-019 Entra na sala de reuniao por video")
    public ReuniaoResposta entrar(@PathVariable String salaId,
                                  @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ReuniaoResposta.de(service.entrarNaSala(salaId, usuario.getId()), usuario.getId());
    }

    @PostMapping("/reunioes/{salaId}/encerrar")
    @Operation(summary = "Encerra a reuniao")
    public ResponseEntity<Void> encerrar(@PathVariable String salaId,
                                         @AuthenticationPrincipal UsuarioAutenticado usuario) {
        service.encerrarSala(salaId, usuario.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reunioes/{salaId}/traducao")
    @Operation(summary = "RF-020 Traduz uma fala durante a reuniao")
    public Map<String, String> traduzir(@PathVariable String salaId,
                                        @RequestBody @Valid TraducaoRequisicao requisicao,
                                        @AuthenticationPrincipal UsuarioAutenticado usuario) {
        String traducao = service.traduzirFala(salaId, usuario.getId(),
                requisicao.fala(), requisicao.idiomaDestino());
        return Map.of("original", requisicao.fala(), "traducao", traducao,
                "aviso", "A traducao e um apoio e pode conter imprecisoes (RN-014)");
    }
}
