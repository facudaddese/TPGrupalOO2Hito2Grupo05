package com.unla.grupo5.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentaDTO {
    private Long idFestival;
    private String nombreFestival;
    private Long idUnidadVenta;
    private String nombreUnidadVenta;
    private String codigoUnidadVenta;
    private double recaudacionTotal;
    private int cantidadPedido;
    private boolean participo;
}
