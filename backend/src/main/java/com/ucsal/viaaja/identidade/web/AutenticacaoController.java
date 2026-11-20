package com.ucsal.viaaja.identidade.web;

import com.ucsal.viaaja.identidade.domain.Usuario;
import com.ucsal.viaaja.identidade.domain.Viajante;
import com.ucsal.viaaja.identidade.security.TokenService;
import com.ucsal.viaaja.identidade.security.UsuarioAutenticado;
import com.ucsal.viaaja.identidade.service.UsuarioService;
import com.ucsal.viaaja.identidade.web.dto.CadastroViajanteRequisicao;
import com.ucsal.viaaja.identidade.web.dto.LoginRequisicao;
import com.ucsal.viaaja.identidade.web.dto.TokenResposta;
import com.ucsal.viaaja.identidade.web.dto.UsuarioResposta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacao", description = "RF-001 e RF-002 Cadastro, login e recuperação de acesso")
public class AutenticacaoController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AutenticacaoController(UsuarioService usuarioService,
                                  AuthenticationManager authenticationManager,
                                  TokenService tokenService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public record RecuperacaoRequisicao(@NotBlank @Email String email) {
    }

    public record RedefinicaoRequisicao(@NotBlank String token, @NotBlank @Size(min = 8, max = 72) String novaSenha) {
    }

    @PostMapping("/cadastro")
    @Operation(summary = "RF-001 Cadastra um novo viajante")
    public ResponseEntity<UsuarioResposta> cadastrar(@RequestBody @Valid CadastroViajanteRequisicao requisicao) {
        Viajante viajante = usuarioService.cadastrarViajante(
                requisicao.nome(), requisicao.email(), requisicao.senha(), requisicao.cpf(),
                requisicao.telefone(), requisicao.dataNascimento(), requisicao.cidadeAproximada());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResposta.de(viajante));
    }

    @PostMapping("/confirmar")
    @Operation(summary = "RF-001 Confirma a conta a partir do token enviado por e-mail")
    public ResponseEntity<Map<String, String>> confirmar(@RequestParam String token) {
        usuarioService.confirmarConta(token);
        return ResponseEntity.ok(Map.of("mensagem", "Conta confirmada com sucesso"));
    }

    @PostMapping("/login")
    @Operation(summary = "RF-002 Autentica por e-mail e senha")
    public ResponseEntity<TokenResposta> login(@RequestBody @Valid LoginRequisicao requisicao) {
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requisicao.email(), requisicao.senha()));
        Usuario usuario = ((UsuarioAutenticado) autenticacao.getPrincipal()).getUsuario();
        String token = tokenService.gerar(usuario);
        return ResponseEntity.ok(new TokenResposta(token, "Bearer", tokenService.expiracaoEmSegundos(),
                usuario.getId(), usuario.getNome(), usuario.getPerfil().name()));
    }

    @PostMapping("/recuperar-senha")
    @Operation(summary = "RF-002 Solicita o código de recuperação de senha")
    public ResponseEntity<Map<String, String>> recuperar(@RequestBody @Valid RecuperacaoRequisicao requisicao) {
        usuarioService.solicitarRecuperacaoDeSenha(requisicao.email());
        return ResponseEntity.ok(Map.of("mensagem",
                "Se o e-mail estiver cadastrado, o código de recuperação será enviado"));
    }

    @PostMapping("/redefinir-senha")
    @Operation(summary = "RF-002 Redefine a senha a partir do código de recuperação")
    public ResponseEntity<Map<String, String>> redefinir(@RequestBody @Valid RedefinicaoRequisicao requisicao) {
        usuarioService.redefinirSenha(requisicao.token(), requisicao.novaSenha());
        return ResponseEntity.ok(Map.of("mensagem", "Senha redefinida com sucesso"));
    }
}
