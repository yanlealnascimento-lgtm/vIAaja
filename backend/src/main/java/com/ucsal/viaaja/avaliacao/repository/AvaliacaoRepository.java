package com.ucsal.viaaja.avaliacao.repository;

import com.ucsal.viaaja.avaliacao.domain.Avaliacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByDestinoIdAndOcultaFalseOrderByDataDesc(Long destinoId);

    List<Avaliacao> findByHospedagemIdAndOcultaFalseOrderByDataDesc(Long hospedagemId);

    List<Avaliacao> findByOcultaFalseOrderByDataDesc();

    boolean existsByAutorIdAndDestinoId(Long autorId, Long destinoId);

    @Query("select avg(a.nota) from Avaliacao a where a.destino.id = :destinoId and a.oculta = false")
    Double mediaDoDestino(@Param("destinoId") Long destinoId);
}
