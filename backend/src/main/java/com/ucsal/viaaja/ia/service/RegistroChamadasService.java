package com.ucsal.viaaja.ia.service;

import com.ucsal.viaaja.ia.domain.RegistroChamadaIA;
import com.ucsal.viaaja.ia.repository.RegistroChamadaIARepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroChamadasService {

    private static final Logger log = LoggerFactory.getLogger(RegistroChamadasService.class);
    private static final int TAMANHO_MAXIMO_DETALHE = 300;

    private final RegistroChamadaIARepository registroRepository;

    public RegistroChamadasService(RegistroChamadaIARepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String funcionalidade, String provedor, Long usuarioId, long tempoMs, String status,
                          String detalhe) {
        try {
            String detalheCurto = detalhe != null && detalhe.length() > TAMANHO_MAXIMO_DETALHE
                    ? detalhe.substring(0, TAMANHO_MAXIMO_DETALHE) : detalhe;
            registroRepository.save(new RegistroChamadaIA(funcionalidade, provedor, usuarioId, tempoMs, status,
                    detalheCurto, false));
        } catch (RuntimeException ex) {
            log.warn("Não foi possível registrar a chamada de IA {}", funcionalidade, ex);
        }
    }
}
