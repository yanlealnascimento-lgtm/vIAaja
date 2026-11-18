package com.ucsal.viaaja.identidade.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "viajante")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Viajante extends Usuario {

    private String cpf;

    private LocalDate dataNascimento;

    private String cidadeAproximada;

    @Embedded
    private PreferenciaPrivacidade preferenciaPrivacidade = new PreferenciaPrivacidade();

    protected Viajante() {
    }

    public Viajante(String nome, String email, String senhaHash, String telefone, String cpf,
                    LocalDate dataNascimento, String cidadeAproximada) {
        super(nome, email, senhaHash, telefone, Perfil.VIAJANTE);
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.cidadeAproximada = cidadeAproximada;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getCidadeAproximada() {
        return cidadeAproximada;
    }

    public void setCidadeAproximada(String cidadeAproximada) {
        this.cidadeAproximada = cidadeAproximada;
    }

    public PreferenciaPrivacidade getPreferenciaPrivacidade() {
        return preferenciaPrivacidade;
    }

    public void setPreferenciaPrivacidade(PreferenciaPrivacidade preferenciaPrivacidade) {
        this.preferenciaPrivacidade = preferenciaPrivacidade;
    }
}
