package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.UnidadDeVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnidadDeVentaRepository extends JpaRepository<UnidadDeVenta, Long> {

    List<UnidadDeVenta> findByFestivalId(Long idFestival);

    boolean existsByCodigoAndFestivalId(String codigo, Long idFestival);

    // Igual que la anterior pero ignorando la propia unidad (para la modificación)
    boolean existsByCodigoAndFestivalIdAndIdNot(String codigo, Long idFestival, Long id);
}
