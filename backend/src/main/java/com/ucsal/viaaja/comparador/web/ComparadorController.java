package com.ucsal.viaaja.comparador.web;

import com.ucsal.viaaja.comparador.domain.ComparacaoCusto;
import com.ucsal.viaaja.comparador.domain.OpcaoDeslocamento;
import com.ucsal.viaaja.comparador.service.ComparadorService;
import com.ucsal.viaaja.identidade.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comparacoes")
@Tag(name = "Comparador", description = "RF-004 Comparacao de custos entre aviao, onibus e carro")
public class ComparadorController {

    private final ComparadorService comparadorService;

    public ComparadorController(ComparadorService comparadorService) {
        this.comparadorService = comparadorService;
    }

    public record ComparacaoRequisicao(
            @NotBlank String origem,
            @NotBlank String destino,
            @NotNull @Future LocalDate data,
            @Min(1) @Max(9) int passageiros) {
    }

    public record OpcaoResposta(
            String modal,
            BigDecimal custoTotal,
            boolean estimado,
            boolean disponivel,
            String detalhe,
            boolean maisBarata) {
    }

    public record ComparacaoResposta(
            Long id,
            String origem,
            String destino,
            LocalDate data,
            int passageiros,
            LocalDateTime consultadoEm,
            String modalMaisBarato,
            List<OpcaoResposta> opcoes) {

        static ComparacaoResposta de(ComparacaoCusto c) {
            String maisBarato = c.getModalMaisBarato() == null ? null : c.getModalMaisBarato().name();
            List<OpcaoResposta> opcoes = c.getOpcoes().stream()
                    .map(o -> new OpcaoResposta(o.getModal().name(), o.getCustoTotal(), o.isEstimado(),
                            o.isDisponivel(), o.getDetalhe(), ehMaisBarata(o, c)))
                    .toList();
            return new ComparacaoResposta(c.getId(), c.getOrigem(), c.getDestino(), c.getData(),
                    c.getPassageiros(), c.getConsultadoEm(), maisBarato, opcoes);
        }

        private static boolean ehMaisBarata(OpcaoDeslocamento opcao, ComparacaoCusto comparacao) {
            return comparacao.getModalMaisBarato() != null
                    && comparacao.getModalMaisBarato() == opcao.getModal();
        }
    }

    @PostMapping
    @Operation(summary = "RF-004 Compara o custo total por aviao, onibus e carro")
    public ComparacaoResposta comparar(@RequestBody @Valid ComparacaoRequisicao requisicao,
                                       @AuthenticationPrincipal UsuarioAutenticado usuario) {
        Long solicitante = usuario == null ? null : usuario.getId();
        return ComparacaoResposta.de(comparadorService.comparar(solicitante, requisicao.origem(),
                requisicao.destino(), requisicao.data(), requisicao.passageiros()));
    }

    @GetMapping("/historico")
    @Operation(summary = "Historico das ultimas comparacoes do viajante")
    public List<ComparacaoResposta> historico(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return comparadorService.historico(usuario.getId()).stream()
                .map(ComparacaoResposta::de).toList();
    }
}
