package com.ucsal.viaaja.identidade.repository;

import com.ucsal.viaaja.identidade.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByTokenConfirmacao(String token);

    Optional<Usuario> findByTokenRecuperacao(String token);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findAllByOrderByNomeAsc();
}
