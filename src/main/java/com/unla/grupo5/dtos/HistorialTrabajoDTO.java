package com.unla.grupo5.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// Una fila del historial de trabajo del empleado: un festival y la unidad en la que trabajó (no se persiste)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialTrabajoDTO {
    private String nombreFestival;
    private String temporada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private String nombreUnidad;
    private String codigoUnidad;
    private String tipoUnidad;

    // true si es la unidad en la que trabaja actualmente
    private boolean actual;
}
