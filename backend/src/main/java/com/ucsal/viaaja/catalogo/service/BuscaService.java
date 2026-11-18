package com.ucsal.viaaja.catalogo.service;

import com.ucsal.viaaja.catalogo.domain.Hospedagem;
import com.ucsal.viaaja.catalogo.domain.ModalTransporte;
import com.ucsal.viaaja.catalogo.domain.Passagem;
import com.ucsal.viaaja.catalogo.repository.HospedagemRepository;
import com.ucsal.viaaja.catalogo.repository.PassagemRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuscaService {

    private final PassagemRepository passagemRepository;
    private final HospedagemRepository hospedagemRepository;

    public BuscaService(PassagemRepository passagemRepository, HospedagemRepository hospedagemRepository) {
        this.passagemRepository = passagemRepository;
        this.hospedagemRepository = hospedagemRepository;
    }

    @Transactional(readOnly = true)
    public Page<Passagem> buscarPassagens(String origem, String destino, LocalDate data, int passageiros,
                                          ModalTransporte modal, BigDecimal precoMaximo, Integer paradasMaximas,
                                          String ordenarPor, int pagina, int tamanho) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(LocalTime.MAX);
        return passagemRepository.buscar(origem, destino, inicio, fim, passageiros, modal,
                precoMaximo, paradasMaximas, PageRequest.of(pagina, tamanho, ordenacaoPassagem(ordenarPor)));
    }

    @Transactional(readOnly = true)
    public Page<Hospedagem> buscarHospedagens(String cidade, int hospedes, BigDecimal precoMaximo,
                                              Integer categoriaMinima, String ordenarPor,
                                              int pagina, int tamanho) {
        return hospedagemRepository.buscar(cidade, hospedes, precoMaximo, categoriaMinima,
                PageRequest.of(pagina, tamanho, ordenacaoHospedagem(ordenarPor)));
    }

    private Sort ordenacaoPassagem(String ordenarPor) {
        if (ordenarPor == null) {
            return Sort.by(Sort.Direction.ASC, "preco");
        }
        return switch (ordenarPor.toLowerCase()) {
            case "duracao" -> Sort.by(Sort.Direction.ASC, "dataChegada");
            case "partida" -> Sort.by(Sort.Direction.ASC, "dataPartida");
            case "paradas" -> Sort.by(Sort.Direction.ASC, "numeroParadas");
            default -> Sort.by(Sort.Direction.ASC, "preco");
        };
    }

    private Sort ordenacaoHospedagem(String ordenarPor) {
        if (ordenarPor == null) {
            return Sort.by(Sort.Direction.ASC, "preco");
        }
        return switch (ordenarPor.toLowerCase()) {
            case "categoria" -> Sort.by(Sort.Direction.DESC, "categoria");
            default -> Sort.by(Sort.Direction.ASC, "preco");
        };
    }
}
