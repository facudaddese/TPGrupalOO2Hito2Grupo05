package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Long> {

    List<Plato> findByUnidadDeVentaId(Long idUnidadVenta);

    // El nombre de un plato no puede repetirse dentro de la misma unidad de venta
    boolean existsByNombreIgnoreCaseAndUnidadDeVentaId(String nombre, Long idUnidadVenta);

    // Igual que la anterior pero ignorando el propio plato (para la modificación)
    boolean existsByNombreIgnoreCaseAndUnidadDeVentaIdAndIdNot(String nombre, Long idUnidadVenta, Long id);
}
