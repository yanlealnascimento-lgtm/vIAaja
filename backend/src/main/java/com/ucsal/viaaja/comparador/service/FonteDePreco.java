package com.ucsal.viaaja.comparador.service;

import com.ucsal.viaaja.catalogo.domain.ModalTransporte;
import com.ucsal.viaaja.comparador.domain.OpcaoDeslocamento;
import java.time.LocalDate;

public interface FonteDePreco {

    ModalTransporte modalAtendido();

    OpcaoDeslocamento cotar(String origem, String destino, LocalDate data, int passageiros);
}
