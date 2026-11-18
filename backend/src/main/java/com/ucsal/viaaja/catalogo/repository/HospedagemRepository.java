package com.ucsal.viaaja.catalogo.repository;

import com.ucsal.viaaja.catalogo.domain.Hospedagem;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HospedagemRepository extends JpaRepository<Hospedagem, Long> {

    @Query("select h from Hospedagem h where h.ativa = true "
            + "and lower(h.destino.cidade) like lower(concat('%', :cidade, '%')) "
            + "and h.disponibilidade >= :hospedes "
            + "and (:precoMaximo is null or h.preco <= :precoMaximo) "
            + "and (:categoriaMinima is null or h.categoria >= :categoriaMinima)")
    Page<Hospedagem> buscar(@Param("cidade") String cidade,
                            @Param("hospedes") int hospedes,
                            @Param("precoMaximo") BigDecimal precoMaximo,
                            @Param("categoriaMinima") Integer categoriaMinima,
                            Pageable pageable);

    List<Hospedagem> findByParceiroId(Long parceiroId);

    List<Hospedagem> findByDestinoId(Long destinoId);
}
