package com.ucsal.viaaja.catalogo.web;

import com.ucsal.viaaja.catalogo.domain.ModalTransporte;
import com.ucsal.viaaja.catalogo.service.BuscaService;
import com.ucsal.viaaja.catalogo.web.dto.HospedagemResposta;
import com.ucsal.viaaja.catalogo.web.dto.PassagemResposta;
import com.ucsal.viaaja.common.dto.PaginaResposta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/busca")
@Tag(name = "Busca", description = "RF-005, RF-007 e RF-009 Busca de passagens e hospedagens com filtros")
public class BuscaController {

    private final BuscaService buscaService;

    public BuscaController(BuscaService buscaService) {
        this.buscaService = buscaService;
    }

    @GetMapping("/passagens")
    @Operation(summary = "RF-005 Busca passagens aereas e rodoviarias")
    public PaginaResposta<PassagemResposta> passagens(
            @RequestParam String origem,
            @RequestParam String destino,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(defaultValue = "1") int passageiros,
            @RequestParam(required = false) ModalTransporte modal,
            @RequestParam(required = false) BigDecimal precoMaximo,
            @RequestParam(required = false) Integer paradasMaximas,
            @RequestParam(required = false) String ordenarPor,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        return PaginaResposta.de(buscaService
                .buscarPassagens(origem, destino, data, passageiros, modal, precoMaximo,
                        paradasMaximas, ordenarPor, pagina, tamanho)
                .map(PassagemResposta::de));
    }

    @GetMapping("/hospedagens")
    @Operation(summary = "RF-007 Busca hoteis e pousadas")
    public PaginaResposta<HospedagemResposta> hospedagens(
            @RequestParam String cidade,
            @RequestParam(defaultValue = "1") int hospedes,
            @RequestParam(required = false) BigDecimal precoMaximo,
            @RequestParam(required = false) Integer categoriaMinima,
            @RequestParam(required = false) String ordenarPor,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        return PaginaResposta.de(buscaService
                .buscarHospedagens(cidade, hospedes, precoMaximo, categoriaMinima, ordenarPor, pagina, tamanho)
                .map(HospedagemResposta::de));
    }
}
