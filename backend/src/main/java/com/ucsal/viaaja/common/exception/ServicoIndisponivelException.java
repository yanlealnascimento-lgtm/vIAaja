package com.ucsal.viaaja.common.exception;

public class ServicoIndisponivelException extends RuntimeException {

    private final String servico;

    public ServicoIndisponivelException(String servico, String mensagem) {
        super(mensagem);
        this.servico = servico;
    }

    public String getServico() {
        return servico;
    }
}
