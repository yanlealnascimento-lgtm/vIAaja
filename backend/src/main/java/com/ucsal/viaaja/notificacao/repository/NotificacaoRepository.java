package com.ucsal.viaaja.notificacao.repository;

import com.ucsal.viaaja.notificacao.domain.Notificacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByDestinatarioIdOrderByDataDesc(Long destinatarioId);

    long countByDestinatarioIdAndLidaFalse(Long destinatarioId);
}
