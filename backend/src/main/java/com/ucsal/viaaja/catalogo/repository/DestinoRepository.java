package com.ucsal.viaaja.catalogo.repository;

import com.ucsal.viaaja.catalogo.domain.Destino;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinoRepository extends JpaRepository<Destino, Long> {

    List<Destino> findByCidadeContainingIgnoreCase(String cidade);

    Optional<Destino> findFirstByCidadeIgnoreCase(String cidade);
}
