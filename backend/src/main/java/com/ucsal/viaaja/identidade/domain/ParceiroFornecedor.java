package com.ucsal.viaaja.identidade.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "parceiro_fornecedor")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class ParceiroFornecedor extends Usuario {

    private String razaoSocial;

    private String cnpj;

    @Enumerated(EnumType.STRING)
    private TipoParceiro tipoParceiro;

    private boolean aprovado = false;

    public enum TipoParceiro {
        COMPANHIA_AEREA,
        VIACAO_RODOVIARIA,
        HOTELARIA
    }

    protected ParceiroFornecedor() {
    }

    public ParceiroFornecedor(String nome, String email, String senhaHash, String telefone,
                              String razaoSocial, String cnpj, TipoParceiro tipoParceiro) {
        super(nome, email, senhaHash, telefone, Perfil.PARCEIRO);
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.tipoParceiro = tipoParceiro;
    }

    public void aprovar() {
        this.aprovado = true;
    }

    public void reprovar() {
        this.aprovado = false;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public TipoParceiro getTipoParceiro() {
        return tipoParceiro;
    }

    public boolean isAprovado() {
        return aprovado;
    }
}
