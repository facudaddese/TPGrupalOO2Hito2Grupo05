package com.unla.grupo5.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnidadDeVentaRequestDTO {

    // ************ Datos de la unidad de venta ************
    @NotNull(message = "Debe seleccionar un festival")
    private Long idFestival;

    @NotBlank(message = "El nombre comercial es obligatorio")
    private String nombreComercial;

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotNull(message = "La superficie es obligatoria")
    @Min(value = 1, message = "La superficie debe ser mayor a 0")
    private Integer superficie;

    // ************ Tipo de unidad ************
    // FOODTRUCK o PUESTO_DESARMABLE
    @NotBlank(message = "Debe seleccionar el tipo de unidad")
    private String tipoUnidad;

    // Solo Food Truck (se validan en el service según el tipo elegido)
    private String patente;

    private boolean conexion;

    // Solo Puesto Desarmable (se validan en el service según el tipo elegido)
    private Integer cantidadCarpas;

    private Double tiempoMontaje;

    // ************ Datos del usuario responsable ************
    @NotBlank(message = "El e-mail es obligatorio")
    @Email(message = "Debe ser un e-mail válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    // El rol no viene del formulario: el service siempre lo crea como RESPONSABLE
}
