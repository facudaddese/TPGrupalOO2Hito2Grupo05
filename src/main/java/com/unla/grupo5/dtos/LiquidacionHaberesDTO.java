package com.unla.grupo5.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LiquidacionHaberesDTO {
    private Long idEmpleado;
    private String nombreCompleto;
    private String tipoEmpleado;
    private double sueldoCalculado;

}
