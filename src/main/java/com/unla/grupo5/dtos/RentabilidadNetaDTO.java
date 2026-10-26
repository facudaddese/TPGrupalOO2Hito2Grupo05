package com.unla.grupo5.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentabilidadNetaDTO {

    private double totalVentas;

    private double costoPlatos;

    private double totalSueldos;

    private double canon;

    private double rentabilidadNeta;

}
