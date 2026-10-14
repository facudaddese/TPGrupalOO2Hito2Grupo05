package com.unla.grupo5.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// Datos que se pueden modificar de una unidad de venta ya registrada
@Getter
@Setter
public class UnidadDeVentaModificarDTO {

    @NotBlank(message = "El nombre comercial es obligatorio")
    private String nombreComercial;

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotNull(message = "La superficie es obligatoria")
    @Min(value = 1, message = "La superficie debe ser mayor a 0")
    private Integer superficie;
}
