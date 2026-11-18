package com.ucsal.viaaja.catalogo.repository;

import com.ucsal.viaaja.catalogo.domain.Atividade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    List<Atividade> findByDestinoId(Long destinoId);

    List<Atividade> findByDestinoIdAndCategoriaIn(Long destinoId, List<String> categorias);
}
