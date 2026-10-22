package com.unla.grupo5.services;

import com.unla.grupo5.dtos.PedidoRequestDTO;
import com.unla.grupo5.entities.Pedido;

import java.util.List;

public interface PedidoService {
    Pedido registrarPedido(PedidoRequestDTO dto);

    List<Pedido> traerPorUnidad(Long idUnidadVenta);
}
