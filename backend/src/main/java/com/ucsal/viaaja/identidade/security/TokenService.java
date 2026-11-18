package com.ucsal.viaaja.identidade.security;

import com.ucsal.viaaja.config.PropriedadesAplicacao;
import com.ucsal.viaaja.identidade.domain.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final SecretKey chave;
    private final long expiracaoMinutos;

    public TokenService(PropriedadesAplicacao propriedades) {
        this.chave = Keys.hmacShaKeyFor(propriedades.getJwt().getSegredo().getBytes(StandardCharsets.UTF_8));
        this.expiracaoMinutos = propriedades.getJwt().getExpiracaoMinutos();
    }

    public String gerar(Usuario usuario) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .claim("perfil", usuario.getPerfil().name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(expiracaoMinutos, ChronoUnit.MINUTES)))
                .signWith(chave)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public long expiracaoEmSegundos() {
        return expiracaoMinutos * 60;
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
