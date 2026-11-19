package com.ucsal.viaaja.social.repository;

import com.ucsal.viaaja.social.domain.Convite;
import com.ucsal.viaaja.social.domain.StatusConvite;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConviteRepository extends JpaRepository<Convite, Long> {

    List<Convite> findByDestinatarioIdOrderByDataEnvioDesc(Long destinatarioId);

    List<Convite> findByRemetenteIdOrderByDataEnvioDesc(Long remetenteId);

    long countByRemetenteIdAndDestinatarioIdAndDataEnvioAfter(Long remetenteId, Long destinatarioId,
                                                              LocalDateTime inicio);

    boolean existsByRemetenteIdAndDestinatarioIdAndStatus(Long remetenteId, Long destinatarioId,
                                                          StatusConvite status);
}
