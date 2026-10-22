package com.unla.grupo5.dtos;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// Datos que se completan en el formulario de alta de un pedido
@Getter
@Setter
public class PedidoRequestDTO {

    @Valid
    private List<ItemPedidoRequestDTO> items = new ArrayList<>();

    // Lo completa el controlador con la unidad del responsable logueado (no viene del formulario)
    private Long idUnidadVenta;
}
