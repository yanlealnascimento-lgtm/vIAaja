package com.ucsal.viaaja.ia;

import com.ucsal.viaaja.ia.dto.DadosGeracaoRoteiro;
import com.ucsal.viaaja.ia.dto.ParametrosBusca;
import com.ucsal.viaaja.ia.dto.ResumoAvaliacoes;
import com.ucsal.viaaja.ia.dto.RoteiroSugerido;
import java.util.List;

public interface ServicoIA {

    String nome();

    ParametrosBusca interpretarPedido(String textoLivre);

    RoteiroSugerido gerarItinerario(DadosGeracaoRoteiro dados);

    ResumoAvaliacoes resumirAvaliacoes(String assunto, List<String> comentarios);

    List<String> recomendarDestinos(List<String> historico, List<String> interesses);

    String traduzir(String fala, String idiomaDestino);
}
