package com.ucsal.viaaja.notificacao.web;

import com.ucsal.viaaja.identidade.security.UsuarioAutenticado;
import com.ucsal.viaaja.notificacao.domain.Notificacao;
import com.ucsal.viaaja.notificacao.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificacoes")
@Tag(name = "Notificacoes", description = "RF-025 Notificacoes ao viajante")
public class NotificacaoController {

    private final NotificacaoService service;

    public NotificacaoController(NotificacaoService service) {
        this.service = service;
    }

    public record NotificacaoResposta(Long id, String tipo, String mensagem, boolean lida, LocalDateTime data) {
        static NotificacaoResposta de(Notificacao n) {
            return new NotificacaoResposta(n.getId(), n.getTipo().name(), n.getMensagem(), n.isLida(), n.getData());
        }
    }

    @GetMapping
    @Operation(summary = "Lista as notificacoes do usuario autenticado")
    public List<NotificacaoResposta> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return service.listar(usuario.getId()).stream().map(NotificacaoResposta::de).toList();
    }

    @GetMapping("/nao-lidas")
    @Operation(summary = "Quantidade de notificacoes nao lidas")
    public Map<String, Long> naoLidas(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return Map.of("total", service.naoLidas(usuario.getId()));
    }

    @PatchMapping("/{id}/lida")
    @Operation(summary = "Marca uma notificacao como lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id,
                                               @AuthenticationPrincipal UsuarioAutenticado usuario) {
        service.marcarComoLida(id, usuario.getId());
        return ResponseEntity.noContent().build();
    }
}
