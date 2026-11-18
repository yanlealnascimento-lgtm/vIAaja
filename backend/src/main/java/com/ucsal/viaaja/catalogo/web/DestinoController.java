package com.ucsal.viaaja.catalogo.web;

import com.ucsal.viaaja.catalogo.domain.Atividade;
import com.ucsal.viaaja.catalogo.repository.AtividadeRepository;
import com.ucsal.viaaja.catalogo.repository.DestinoRepository;
import com.ucsal.viaaja.catalogo.web.dto.DestinoResposta;
import com.ucsal.viaaja.common.exception.RecursoNaoEncontradoException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/destinos")
@Tag(name = "Destinos", description = "RF-004 Consulta de informacoes do destino")
public class DestinoController {

    private final DestinoRepository destinoRepository;
    private final AtividadeRepository atividadeRepository;

    public DestinoController(DestinoRepository destinoRepository, AtividadeRepository atividadeRepository) {
        this.destinoRepository = destinoRepository;
        this.atividadeRepository = atividadeRepository;
    }

    public record AtividadeResposta(Long id, String nome, String categoria, BigDecimal custoMedio) {
        static AtividadeResposta de(Atividade a) {
            return new AtividadeResposta(a.getId(), a.getNome(), a.getCategoria(), a.getCustoMedio());
        }
    }

    @GetMapping
    @Operation(summary = "Lista destinos, com filtro opcional por cidade")
    public List<DestinoResposta> listar(@RequestParam(required = false) String cidade) {
        var destinos = (cidade == null || cidade.isBlank())
                ? destinoRepository.findAll()
                : destinoRepository.findByCidadeContainingIgnoreCase(cidade);
        return destinos.stream().map(DestinoResposta::de).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha um destino")
    public DestinoResposta porId(@PathVariable Long id) {
        return destinoRepository.findById(id).map(DestinoResposta::de)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Destino", id));
    }

    @GetMapping("/{id}/atividades")
    @Operation(summary = "Lista as atividades cadastradas para o destino")
    public List<AtividadeResposta> atividades(@PathVariable Long id) {
        return atividadeRepository.findByDestinoId(id).stream().map(AtividadeResposta::de).toList();
    }
}
