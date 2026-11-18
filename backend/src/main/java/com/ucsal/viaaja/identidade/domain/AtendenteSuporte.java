package com.ucsal.viaaja.identidade.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "atendente_suporte")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class AtendenteSuporte extends Usuario {

    private String matricula;

    protected AtendenteSuporte() {
    }

    public AtendenteSuporte(String nome, String email, String senhaHash, String telefone, String matricula) {
        super(nome, email, senhaHash, telefone, Perfil.SUPORTE);
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }
}
