package com.ucsal.viaaja.identidade.web.dto;

import com.ucsal.viaaja.identidade.domain.Usuario;
import com.ucsal.viaaja.identidade.domain.Viajante;

public record UsuarioResposta(
        Long id,
        String nome,
        String email,
        String telefone,
        String perfil,
        boolean ativo,
        String cidadeAproximada,
        Boolean visivelComoProximo,
        String quemPodeConvidar) {

    public static UsuarioResposta de(Usuario usuario) {
        if (usuario instanceof Viajante viajante) {
            return new UsuarioResposta(
                    viajante.getId(), viajante.getNome(), viajante.getEmail(), viajante.getTelefone(),
                    viajante.getPerfil().name(), viajante.isAtivo(), viajante.getCidadeAproximada(),
                    viajante.getPreferenciaPrivacidade().isVisivelComoProximo(),
                    viajante.getPreferenciaPrivacidade().getQuemPodeConvidar().name());
        }
        return new UsuarioResposta(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTelefone(),
                usuario.getPerfil().name(), usuario.isAtivo(), null, null, null);
    }
}
