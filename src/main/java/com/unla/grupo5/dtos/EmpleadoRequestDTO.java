package com.unla.grupo5.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmpleadoRequestDTO {

    // Controla que el usuario no envie la casilla vacia
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotNull(message = "El DNI es obligatorio")
    @Min(value = 1000000, message = "DNI no válido")
    private Integer dni;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;

    @Email(message = "Debe ingresar un email válido")
    @NotBlank(message = "El email es obligatorio para crear el usuario")
    private String email;

    @NotNull(message = "Debe asignar una Unidad de Venta")
    private Integer idUnidadVenta;

    // Si es cocinero o cajero
    @NotBlank(message = "Debe seleccionar un tipo de empleado")
    private String tipoEmpleado;

    private Long idCategoriaCocinero;
    private Long idTurnoCajero;
}