package com.ucsal.viaaja.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viaaja")
public class PropriedadesAplicacao {

    private Jwt jwt = new Jwt();
    private Ia ia = new Ia();
    private Comparador comparador = new Comparador();
    private String urlFrontend = "http://localhost:5173";

    public static class Jwt {
        private String segredo = "troque-este-segredo-em-producao-viaaja-ucsal-2026-chave";
        private long expiracaoMinutos = 480;

        public String getSegredo() {
            return segredo;
        }

        public void setSegredo(String segredo) {
            this.segredo = segredo;
        }

        public long getExpiracaoMinutos() {
            return expiracaoMinutos;
        }

        public void setExpiracaoMinutos(long expiracaoMinutos) {
            this.expiracaoMinutos = expiracaoMinutos;
        }
    }

    public static class Ia {
        private String provedor = "simulado";
        private String url = "https://api.openai.com/v1/chat/completions";
        private String chave = "";
        private String modelo = "gpt-4o-mini";
        private int timeoutSegundos = 8;

        public String getProvedor() {
            return provedor;
        }

        public void setProvedor(String provedor) {
            this.provedor = provedor;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getChave() {
            return chave;
        }

        public void setChave(String chave) {
            this.chave = chave;
        }

        public String getModelo() {
            return modelo;
        }

        public void setModelo(String modelo) {
            this.modelo = modelo;
        }

        public int getTimeoutSegundos() {
            return timeoutSegundos;
        }

        public void setTimeoutSegundos(int timeoutSegundos) {
            this.timeoutSegundos = timeoutSegundos;
        }
    }

    public static class Comparador {
        private double precoCombustivelPorLitro = 6.10;
        private double consumoMedioKmPorLitro = 11.0;
        private double pedagioPorCemKm = 9.50;

        public double getPrecoCombustivelPorLitro() {
            return precoCombustivelPorLitro;
        }

        public void setPrecoCombustivelPorLitro(double precoCombustivelPorLitro) {
            this.precoCombustivelPorLitro = precoCombustivelPorLitro;
        }

        public double getConsumoMedioKmPorLitro() {
            return consumoMedioKmPorLitro;
        }

        public void setConsumoMedioKmPorLitro(double consumoMedioKmPorLitro) {
            this.consumoMedioKmPorLitro = consumoMedioKmPorLitro;
        }

        public double getPedagioPorCemKm() {
            return pedagioPorCemKm;
        }

        public void setPedagioPorCemKm(double pedagioPorCemKm) {
            this.pedagioPorCemKm = pedagioPorCemKm;
        }
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Ia getIa() {
        return ia;
    }

    public void setIa(Ia ia) {
        this.ia = ia;
    }

    public Comparador getComparador() {
        return comparador;
    }

    public void setComparador(Comparador comparador) {
        this.comparador = comparador;
    }

    public String getUrlFrontend() {
        return urlFrontend;
    }

    public void setUrlFrontend(String urlFrontend) {
        this.urlFrontend = urlFrontend;
    }
}
