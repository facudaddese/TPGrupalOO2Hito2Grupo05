package com.unla.grupo5.services.impl;

import com.unla.grupo5.dtos.ItemPedidoRequestDTO;
import com.unla.grupo5.dtos.PedidoRequestDTO;
import com.unla.grupo5.entities.ItemPedido;
import com.unla.grupo5.entities.Pedido;
import com.unla.grupo5.entities.Plato;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.repositories.PedidoRepository;
import com.unla.grupo5.repositories.PlatoRepository;
import com.unla.grupo5.repositories.UnidadDeVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoService implements com.unla.grupo5.services.PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PlatoRepository platoRepository;

    @Autowired
    private UnidadDeVentaRepository unidadDeVentaRepository;

    // Alta de un pedido con sus ítems; el total lo calcula el sistema
    @Override
    @Transactional
    public Pedido registrarPedido(PedidoRequestDTO dto) {
        // La unidad de venta tiene que existir
        UnidadDeVenta unidad = unidadDeVentaRepository.findById(dto.getIdUnidadVenta())
                .orElseThrow(() -> new IllegalArgumentException("La unidad de venta seleccionada no existe."));

        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDate.now());
        pedido.setUnidadDeVenta(unidad);

        double total = 0;
        for (ItemPedidoRequestDTO itemDTO : dto.getItems()) {
            // Los platos sin cantidad no se incluyen en el pedido
            if (itemDTO.getCantidad() == null || itemDTO.getCantidad() <= 0) {
                continue;
            }

            Plato plato = platoRepository.findById(itemDTO.getIdPlato())
                    .orElseThrow(() -> new IllegalArgumentException("El plato seleccionado no existe."));

            // Solo se pueden pedir platos de la misma unidad de venta
            if (!plato.getUnidadDeVenta().getId().equals(unidad.getId())) {
                throw new IllegalArgumentException("El plato " + plato.getNombre() + " no pertenece a esta unidad de venta.");
            }

            ItemPedido item = new ItemPedido();
            item.setPlato(plato);
            item.setCantidad(itemDTO.getCantidad());
            // Se arman los dos lados de la relación para que el cascade guarde el ítem con su id_pedido
            item.setPedido(pedido);
            pedido.getListaItems().add(item);

            total += plato.getPrecio() * itemDTO.getCantidad();
        }

        // El pedido tiene que tener al menos un plato
        if (pedido.getListaItems().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un plato con cantidad mayor a 0.");
        }

        pedido.setTotal(total);

        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> traerPorUnidad(Long idUnidadVenta) {
        return pedidoRepository.findByUnidadDeVentaIdOrderByFechaDescIdDesc(idUnidadVenta);
    }
}
