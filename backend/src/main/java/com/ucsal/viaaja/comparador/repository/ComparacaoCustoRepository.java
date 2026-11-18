package com.ucsal.viaaja.comparador.repository;

import com.ucsal.viaaja.comparador.domain.ComparacaoCusto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComparacaoCustoRepository extends JpaRepository<ComparacaoCusto, Long> {

    List<ComparacaoCusto> findTop10BySolicitanteIdOrderByConsultadoEmDesc(Long solicitanteId);
}
