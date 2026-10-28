package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUnidadDeVentaIdOrderByFechaDescIdDesc(Long idUnidadVenta);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.listaItems i LEFT JOIN FETCH i.plato WHERE p.unidadDeVenta.id = :idUnidadVenta ORDER BY p.fecha DESC, p.id DESC")
    List<Pedido> findByUnidadDeVentaIdConItems(@Param("idUnidadVenta") Long idUnidadVenta);
}