package com.ucsal.viaaja.identidade.repository;

import com.ucsal.viaaja.identidade.domain.ParceiroFornecedor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParceiroRepository extends JpaRepository<ParceiroFornecedor, Long> {

    Optional<ParceiroFornecedor> findByEmailIgnoreCase(String email);

    List<ParceiroFornecedor> findByAprovado(boolean aprovado);
}
