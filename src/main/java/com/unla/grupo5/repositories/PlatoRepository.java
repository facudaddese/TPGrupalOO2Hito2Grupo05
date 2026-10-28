package com.unla.grupo5.repositories;

import com.unla.grupo5.dtos.PlatoDTO;
import com.unla.grupo5.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Long> {

    List<Plato> findByUnidadDeVentaId(Long idUnidadVenta);

    // Platos de una unidad en un festival, ordenados por la cantidad de pedidos en los que aparecen.
    // Si empatan, primero el que más unidades vendió.
    @Query("SELECT new com.unla.grupo5.dtos.PlatoDTO(p.id, p.nombre, p.precio, p.costoProduccion, " +
            "COUNT(DISTINCT pe.id), SUM(i.cantidad)) " +
            "FROM ItemPedido i JOIN i.plato p JOIN i.pedido pe " +
            "WHERE pe.unidadDeVenta.id = :idUnidad AND pe.unidadDeVenta.festival.id = :idFestival " +
            "GROUP BY p.id, p.nombre, p.precio, p.costoProduccion " +
            "ORDER BY COUNT(DISTINCT pe.id) DESC, SUM(i.cantidad) DESC, p.nombre ASC")
    List<PlatoDTO> rankingPlatosPorPedidos(@Param("idUnidad") Long idUnidad, @Param("idFestival") Long idFestival);

    // El nombre de un plato no puede repetirse dentro de la misma unidad de venta
    boolean existsByNombreIgnoreCaseAndUnidadDeVentaId(String nombre, Long idUnidadVenta);

    // Igual que la anterior pero ignorando el propio plato (para la modificación)
    boolean existsByNombreIgnoreCaseAndUnidadDeVentaIdAndIdNot(String nombre, Long idUnidadVenta, Long id);
}
