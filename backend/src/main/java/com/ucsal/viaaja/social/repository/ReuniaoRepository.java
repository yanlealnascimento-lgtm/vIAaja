package com.ucsal.viaaja.social.repository;

import com.ucsal.viaaja.social.domain.Reuniao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {

    Optional<Reuniao> findBySalaId(String salaId);

    Optional<Reuniao> findByConviteId(Long conviteId);

    @Query("select r from Reuniao r where r.convite.remetente.id = :viajanteId "
            + "or r.convite.destinatario.id = :viajanteId order by r.dataHora desc")
    List<Reuniao> doViajante(@Param("viajanteId") Long viajanteId);
}
