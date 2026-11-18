package com.ucsal.viaaja.identidade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Column(length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(nullable = false)
    private boolean ativo = false;

    @Column(name = "token_confirmacao", length = 60)
    private String tokenConfirmacao;

    @Column(name = "token_recuperacao", length = 60)
    private String tokenRecuperacao;

    @Column(name = "token_recuperacao_expira_em")
    private LocalDateTime tokenRecuperacaoExpiraEm;

    protected Usuario() {
    }

    protected Usuario(String nome, String email, String senhaHash, String telefone, Perfil perfil) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.telefone = telefone;
        this.perfil = perfil;
    }

    public void confirmar() {
        this.ativo = true;
        this.tokenConfirmacao = null;
    }

    public void registrarTokenConfirmacao(String token) {
        this.tokenConfirmacao = token;
        this.ativo = false;
    }

    public void registrarTokenRecuperacao(String token, LocalDateTime expiraEm) {
        this.tokenRecuperacao = token;
        this.tokenRecuperacaoExpiraEm = expiraEm;
    }

    public void trocarSenha(String novoHash) {
        this.senhaHash = novoHash;
        this.tokenRecuperacao = null;
        this.tokenRecuperacaoExpiraEm = null;
    }

    public boolean recuperacaoValida(LocalDateTime agora) {
        return tokenRecuperacao != null
                && tokenRecuperacaoExpiraEm != null
                && agora.isBefore(tokenRecuperacaoExpiraEm);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public String getTokenConfirmacao() {
        return tokenConfirmacao;
    }

    public String getTokenRecuperacao() {
        return tokenRecuperacao;
    }
}
