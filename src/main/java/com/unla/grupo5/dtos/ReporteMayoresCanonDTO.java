package com.unla.grupo5.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Una fila del reporte de unidades con mayor canon (no se persiste)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMayoresCanonDTO {
    private String nombreComercial;
    private String codigo;
    private String tipoUnidad;
    private double canon;
}
