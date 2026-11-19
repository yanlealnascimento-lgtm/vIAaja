package com.ucsal.viaaja.gestao.web;

import com.ucsal.viaaja.catalogo.domain.Hospedagem;
import com.ucsal.viaaja.catalogo.domain.Oferta;
import com.ucsal.viaaja.catalogo.domain.Passagem;
import com.ucsal.viaaja.catalogo.repository.OfertaRepository;
import com.ucsal.viaaja.common.exception.RecursoNaoEncontradoException;
import com.ucsal.viaaja.identidade.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parceiro")
@PreAuthorize("hasRole('PARCEIRO')")
@Tag(name = "Painel do parceiro", description = "RF-023 Gestao de ofertas, precos e disponibilidade")
public class ParceiroController {

    private final OfertaRepository ofertaRepository;

    public ParceiroController(OfertaRepository ofertaRepository) {
        this.ofertaRepository = ofertaRepository;
    }

    public record OfertaResposta(Long id, String tipo, String titulo, String destino,
                                 BigDecimal preco, int disponibilidade, boolean ativa) {
        static OfertaResposta de(Oferta o) {
            String tipo = o instanceof Passagem ? "PASSAGEM" : "HOSPEDAGEM";
            String titulo = o instanceof Passagem p
                    ? p.getEmpresa() + " " + p.getOrigem() + " para " + p.getNomeDestino()
                    : ((Hospedagem) o).getNome();
            return new OfertaResposta(o.getId(), tipo, titulo, o.getDestino().nomeCompleto(),
                    o.getPreco(), o.getDisponibilidade(), o.isAtiva());
        }
    }

    public record AtualizacaoRequisicao(
            @NotNull @Positive BigDecimal preco,
            @NotNull @Min(0) Integer disponibilidade) {
    }

    @GetMapping("/ofertas")
    @Operation(summary = "Lista as ofertas do parceiro autenticado")
    public List<OfertaResposta> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ofertaRepository.findByParceiroId(usuario.getId()).stream().map(OfertaResposta::de).toList();
    }

    @PutMapping("/ofertas/{id}")
    @Operation(summary = "RF-023 Atualiza preco e disponibilidade de uma oferta")
    @Transactional
    public OfertaResposta atualizar(@PathVariable Long id,
                                    @RequestBody @Valid AtualizacaoRequisicao requisicao,
                                    @AuthenticationPrincipal UsuarioAutenticado usuario) {
        Oferta oferta = buscarDoParceiro(id, usuario.getId());
        oferta.atualizarPreco(requisicao.preco());
        oferta.atualizarDisponibilidade(requisicao.disponibilidade());
        return OfertaResposta.de(oferta);
    }

    @PatchMapping("/ofertas/{id}/desativar")
    @Operation(summary = "RF-023 Retira a oferta da plataforma")
    @Transactional
    public ResponseEntity<Void> desativar(@PathVariable Long id,
                                          @AuthenticationPrincipal UsuarioAutenticado usuario) {
        buscarDoParceiro(id, usuario.getId()).desativar();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/ofertas/{id}/ativar")
    @Operation(summary = "RF-023 Reativa uma oferta")
    @Transactional
    public ResponseEntity<Void> ativar(@PathVariable Long id,
                                       @AuthenticationPrincipal UsuarioAutenticado usuario) {
        buscarDoParceiro(id, usuario.getId()).ativar();
        return ResponseEntity.noContent().build();
    }

    private Oferta buscarDoParceiro(Long ofertaId, Long parceiroId) {
        return ofertaRepository.findById(ofertaId)
                .filter(o -> o.getParceiro() != null && o.getParceiro().getId().equals(parceiroId))
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Oferta", ofertaId));
    }
}
