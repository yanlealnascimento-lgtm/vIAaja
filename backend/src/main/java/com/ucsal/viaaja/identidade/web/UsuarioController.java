package com.ucsal.viaaja.identidade.web;

import com.ucsal.viaaja.identidade.security.UsuarioAutenticado;
import com.ucsal.viaaja.identidade.service.UsuarioService;
import com.ucsal.viaaja.identidade.web.dto.PerfilRequisicao;
import com.ucsal.viaaja.identidade.web.dto.PrivacidadeRequisicao;
import com.ucsal.viaaja.identidade.web.dto.UsuarioResposta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "RF-022 Perfil e preferencias de privacidade")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/eu")
    @Operation(summary = "Dados do usuario autenticado")
    public UsuarioResposta eu(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return UsuarioResposta.de(usuarioService.porId(usuario.getId()));
    }

    @PutMapping("/eu")
    @Operation(summary = "Atualiza os dados de perfil do viajante")
    @PreAuthorize("hasRole('VIAJANTE')")
    public UsuarioResposta atualizarPerfil(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                           @RequestBody @Valid PerfilRequisicao requisicao) {
        return UsuarioResposta.de(usuarioService.atualizarPerfil(usuario.getId(),
                requisicao.nome(), requisicao.telefone(), requisicao.cidadeAproximada()));
    }

    @PutMapping("/eu/privacidade")
    @Operation(summary = "RF-022 Define a visibilidade nos recursos sociais")
    @PreAuthorize("hasRole('VIAJANTE')")
    public UsuarioResposta atualizarPrivacidade(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                                @RequestBody @Valid PrivacidadeRequisicao requisicao) {
        return UsuarioResposta.de(usuarioService.atualizarPrivacidade(usuario.getId(),
                requisicao.visivelComoProximo(), requisicao.quemPodeConvidar()));
    }
}
