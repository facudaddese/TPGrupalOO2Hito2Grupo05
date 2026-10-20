package com.unla.grupo5.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionDTO {
    private Long idStaff;
    private String nombreCompleto;
    private Long dni;
    private String rolOEmpleado;
    private LocalDate fechaIngreso;
    private double sueldoBase;
    private double plusOAdicionales;
    private double totalLiquido;
}