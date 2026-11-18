package com.ucsal.viaaja.identidade.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Administrador extends Usuario {

    protected Administrador() {
    }

    public Administrador(String nome, String email, String senhaHash, String telefone) {
        super(nome, email, senhaHash, telefone, Perfil.ADMINISTRADOR);
    }
}
