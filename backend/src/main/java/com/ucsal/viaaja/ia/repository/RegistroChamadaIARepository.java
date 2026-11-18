package com.ucsal.viaaja.ia.repository;

import com.ucsal.viaaja.ia.domain.RegistroChamadaIA;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegistroChamadaIARepository extends JpaRepository<RegistroChamadaIA, Long> {

    List<RegistroChamadaIA> findTop50ByOrderByDataDesc();

    long countByDataAfter(LocalDateTime inicio);

    @Query("select r.funcionalidade, count(r), avg(r.tempoRespostaMs) "
            + "from RegistroChamadaIA r group by r.funcionalidade")
    List<Object[]> consolidarPorFuncionalidade();

    @Query("select count(r) from RegistroChamadaIA r where r.status = :status")
    long contarPorStatus(@Param("status") String status);
}
