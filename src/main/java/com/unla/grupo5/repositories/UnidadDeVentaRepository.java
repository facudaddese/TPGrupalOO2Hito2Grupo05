package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadDeVentaRepository extends JpaRepository<UnidadDeVenta, Long> {

    @Query("SELECT DISTINCT u FROM UnidadDeVenta u LEFT JOIN FETCH u.lstPedidos WHERE u.festival.id = :idFestival")
    List<UnidadDeVenta> findByFestivalIdWithPedidos(@Param("idFestival") Long idFestival);

    @Query("SELECT DISTINCT u FROM UnidadDeVenta u LEFT JOIN FETCH u.lstPedidos WHERE u.id = :idUnidad")
    Optional<UnidadDeVenta> findByIdWithPedidos(@Param("idUnidad") Long idUnidad);

    List<UnidadDeVenta> findByFestivalId(Long idFestival);

    boolean existsByCodigoAndFestivalId(String codigo, Long idFestival);

    // Igual que la anterior pero ignorando la propia unidad (para la modificación)
    boolean existsByCodigoAndFestivalIdAndIdNot(String codigo, Long idFestival, Long id);
    Optional<UnidadDeVenta> findByResponsable(Usuario responsable);
}
