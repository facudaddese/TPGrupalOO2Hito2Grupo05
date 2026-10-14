package com.unla.grupo5.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Datos que se completan en el formulario de alta de un plato
@Getter
@Setter
public class PlatoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "El costo de producción es obligatorio")
    @PositiveOrZero(message = "El costo de producción no puede ser negativo")
    private Double costoProduccion;

    // Lo completa el controlador con la unidad del responsable logueado (no viene del formulario)
    private Long idUnidadVenta;
}
