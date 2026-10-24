package com.unla.grupo5.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class LiquidacionHaberesDTO {
    private Long idEmpleado;

    private String nombreCompleto;

    private String tipoEmpleado;

    private double sueldoBase;

    private String plus;

    private double montoAdicional;

    private double factorDuracion;

    private double sueldoCalculado;

    LocalDate periodoInicio;

    LocalDate periodoFin;

    long diasLiquidacion;


}
