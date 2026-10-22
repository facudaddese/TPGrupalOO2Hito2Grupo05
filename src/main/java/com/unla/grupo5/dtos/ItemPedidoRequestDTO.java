package com.unla.grupo5.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

// Una fila del formulario de alta de pedido: un plato y la cantidad pedida
@Getter
@Setter
public class ItemPedidoRequestDTO {

    @NotNull(message = "El plato es obligatorio")
    private Long idPlato;

    // Si queda en 0 el plato no se incluye en el pedido
    @PositiveOrZero(message = "La cantidad no puede ser negativa")
    private Integer cantidad = 0;
}
