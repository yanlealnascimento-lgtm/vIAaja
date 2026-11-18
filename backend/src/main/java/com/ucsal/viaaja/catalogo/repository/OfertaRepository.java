package com.ucsal.viaaja.catalogo.repository;

import com.ucsal.viaaja.catalogo.domain.Oferta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    List<Oferta> findByParceiroId(Long parceiroId);
}
