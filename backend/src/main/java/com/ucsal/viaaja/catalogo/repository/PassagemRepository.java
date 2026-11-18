package com.ucsal.viaaja.catalogo.repository;

import com.ucsal.viaaja.catalogo.domain.ModalTransporte;
import com.ucsal.viaaja.catalogo.domain.Passagem;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PassagemRepository extends JpaRepository<Passagem, Long> {

    @Query("select p from Passagem p where p.ativa = true "
            + "and lower(p.origem) like lower(concat('%', :origem, '%')) "
            + "and lower(p.nomeDestino) like lower(concat('%', :destino, '%')) "
            + "and p.dataPartida between :inicio and :fim "
            + "and p.assentosDisponiveis >= :passageiros "
            + "and (:modal is null or p.modal = :modal) "
            + "and (:precoMaximo is null or p.preco <= :precoMaximo) "
            + "and (:paradasMaximas is null or p.numeroParadas <= :paradasMaximas)")
    Page<Passagem> buscar(@Param("origem") String origem,
                          @Param("destino") String destino,
                          @Param("inicio") LocalDateTime inicio,
                          @Param("fim") LocalDateTime fim,
                          @Param("passageiros") int passageiros,
                          @Param("modal") ModalTransporte modal,
                          @Param("precoMaximo") java.math.BigDecimal precoMaximo,
                          @Param("paradasMaximas") Integer paradasMaximas,
                          Pageable pageable);

    @Query("select p from Passagem p where p.ativa = true and p.modal = :modal "
            + "and lower(p.origem) like lower(concat('%', :origem, '%')) "
            + "and lower(p.nomeDestino) like lower(concat('%', :destino, '%')) "
            + "and p.dataPartida between :inicio and :fim "
            + "order by p.preco asc")
    List<Passagem> maisBaratasPorModal(@Param("modal") ModalTransporte modal,
                                       @Param("origem") String origem,
                                       @Param("destino") String destino,
                                       @Param("inicio") LocalDateTime inicio,
                                       @Param("fim") LocalDateTime fim);

    List<Passagem> findByParceiroId(Long parceiroId);
}
