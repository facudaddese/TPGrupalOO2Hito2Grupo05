package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Pedidos de una unidad de venta, del más reciente al más antiguo
    List<Pedido> findByUnidadDeVentaIdOrderByFechaDescIdDesc(Long idUnidadVenta);
}
