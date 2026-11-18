package com.ucsal.viaaja.identidade.repository;

import com.ucsal.viaaja.identidade.domain.Viajante;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ViajanteRepository extends JpaRepository<Viajante, Long> {

    Optional<Viajante> findByEmailIgnoreCase(String email);

    boolean existsByCpf(String cpf);

    @Query("select v from Viajante v where v.preferenciaPrivacidade.visivelComoProximo = true "
            + "and v.id <> :idAtual and lower(v.cidadeAproximada) = lower(:cidade)")
    List<Viajante> buscarProximosPorCidade(@Param("cidade") String cidade, @Param("idAtual") Long idAtual);
}
