package com.unla.grupo5.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Plato con sus números de venta, para el reporte de plato estrella (no se persiste)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlatoDTO {
    private Long id;
    private String nombre;
    private double precio;
    private double costoProduccion;

    // En cuántos pedidos distintos aparece el plato
    private Long cantidadPedidos;

    // Cuántas unidades del plato se vendieron en total
    private Long cantidadVendida;
}
