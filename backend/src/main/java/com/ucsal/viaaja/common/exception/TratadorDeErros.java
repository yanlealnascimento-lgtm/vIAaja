package com.ucsal.viaaja.common.exception;

import com.ucsal.viaaja.common.dto.ErroResposta;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException ex) {
        List<ErroResposta.CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ErroResposta.CampoInvalido(e.getField(), e.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest()
                .body(ErroResposta.validacao(400, "Verifique os campos informados", campos));
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResposta> tratarRegra(RegraDeNegocioException ex) {
        return ResponseEntity.unprocessableEntity()
                .body(ErroResposta.de(422, "Regra de negocio", ex.getMessage(), ex.getRegra()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErroResposta.de(404, "Nao encontrado", ex.getMessage()));
    }

    @ExceptionHandler(ServicoIndisponivelException.class)
    public ResponseEntity<ErroResposta> tratarIndisponivel(ServicoIndisponivelException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErroResposta.de(503, "Servico indisponivel: " + ex.getServico(), ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResposta> tratarAcessoNegado(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErroResposta.de(403, "Acesso negado", "Seu perfil nao permite esta operacao"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroResposta> tratarAutenticacao(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErroResposta.de(401, "Nao autenticado", "Credenciais invalidas ou ausentes"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> tratarGeral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErroResposta.de(500, "Erro interno", ex.getMessage()));
    }
}
