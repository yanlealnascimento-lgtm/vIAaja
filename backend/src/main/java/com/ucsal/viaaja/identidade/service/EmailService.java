package com.ucsal.viaaja.identidade.service;

import com.ucsal.viaaja.config.PropriedadesAplicacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final PropriedadesAplicacao propriedades;

    public EmailService(PropriedadesAplicacao propriedades) {
        this.propriedades = propriedades;
    }

    public void enviarConfirmacaoDeConta(String destinatario, String token) {
        String link = propriedades.getUrlFrontend() + "/confirmar-conta?token=" + token;
        log.info("[email] confirmação de conta para {} -> {}", destinatario, link);
    }

    public void enviarRecuperacaoDeSenha(String destinatario, String token) {
        String link = propriedades.getUrlFrontend() + "/redefinir-senha?token=" + token;
        log.info("[email] recuperação de senha para {} -> {}", destinatario, link);
    }
}
