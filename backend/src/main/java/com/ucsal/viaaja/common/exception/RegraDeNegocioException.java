package com.ucsal.viaaja.common.exception;

public class RegraDeNegocioException extends RuntimeException {

    private final String regra;

    public RegraDeNegocioException(String regra, String mensagem) {
        super(mensagem);
        this.regra = regra;
    }

    public RegraDeNegocioException(String mensagem) {
        this(null, mensagem);
    }

    public String getRegra() {
        return regra;
    }
}
