package com.ucsal.viaaja.ia.web;

import com.ucsal.viaaja.ia.dto.ParametrosBusca;
import com.ucsal.viaaja.ia.service.IntegracaoIAService;
import com.ucsal.viaaja.identidade.security.UsuarioAutenticado;
import com.ucsal.viaaja.itinerario.repository.ItinerarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ia")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Assistente de IA", description = "RF-003 e RF-012 Linguagem natural e recomendacoes")
public class AssistenteController {

    private final IntegracaoIAService integracaoIA;
    private final ItinerarioRepository itinerarioRepository;

    public AssistenteController(IntegracaoIAService integracaoIA, ItinerarioRepository itinerarioRepository) {
        this.integracaoIA = integracaoIA;
        this.itinerarioRepository = itinerarioRepository;
    }

    public record PedidoRequisicao(@NotBlank @Size(max = 500) String texto) {
    }

    @PostMapping("/interpretar")
    @Operation(summary = "RF-003 Interpreta um pedido escrito em linguagem natural")
    public ParametrosBusca interpretar(@RequestBody @Valid PedidoRequisicao requisicao,
                                       @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return integracaoIA.interpretarPedido(requisicao.texto(), usuario.getId());
    }

    @GetMapping("/recomendacoes")
    @Operation(summary = "RF-012 Recomenda destinos a partir do historico do viajante")
    public Map<String, Object> recomendacoes(@RequestParam(required = false) List<String> interesses,
                                             @AuthenticationPrincipal UsuarioAutenticado usuario) {
        List<String> historico = itinerarioRepository.destinosDoViajante(usuario.getId());
        List<String> sugeridos = integracaoIA.recomendarDestinos(historico,
                interesses == null ? List.of() : interesses, usuario.getId());
        return Map.of("historico", historico, "recomendacoes", sugeridos);
    }

    @GetMapping("/status")
    @Operation(summary = "Informa qual provedor de IA esta ativo")
    public Map<String, String> status() {
        return Map.of("provedor", integracaoIA.provedorAtivo());
    }
}
