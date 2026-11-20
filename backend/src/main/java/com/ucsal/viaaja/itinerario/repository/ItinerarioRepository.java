package com.ucsal.viaaja.itinerario.repository;

import com.ucsal.viaaja.itinerario.domain.Itinerario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItinerarioRepository extends JpaRepository<Itinerario, Long> {

    List<Itinerario> findByViajanteIdOrderByDataInicioDesc(Long viajanteId);

    @Query("select i from Itinerario i left join fetch i.dias where i.id = :id")
    Optional<Itinerario> buscarComDias(@Param("id") Long id);

    @Query("select distinct i.destino.cidade from Itinerario i where i.viajante.id = :viajanteId")
    List<String> destinosDoViajante(@Param("viajanteId") Long viajanteId);

    @Query("select distinct i.viajante.id from Itinerario i where lower(i.destino.cidade) = lower(:cidade) "
            + "and i.viajante.id <> :idAtual")
    List<Long> viajantesComInteresseNoDestino(@Param("cidade") String cidade, @Param("idAtual") Long idAtual);
}
