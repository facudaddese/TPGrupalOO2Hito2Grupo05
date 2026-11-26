package com.unla.grupo5.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StaffCreateRequestDTO extends EmpleadoRequestDTO {

    @NotNull(message = "El ID de la unidad de venta es obligatorio")
    private Long idUnidadDeVenta;
}