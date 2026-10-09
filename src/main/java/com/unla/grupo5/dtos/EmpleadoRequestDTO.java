package com.unla.grupo5.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class EmpleadoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotNull(message = "El DNI es obligatorio")
    private Long dni;

    @NotBlank(message = "El e-mail es obligatorio")
    @Email(message = "Debe ser un e-mail válido")
    private String email;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaIngreso;

    @NotNull(message = "El sueldo es obligatorio")
    private Double sueldo;

    @NotNull(message = "Debe seleccionar una unidad de venta")
    private Long idUnidadVenta;

    @NotBlank(message = "Debe seleccionar el tipo de empleado")
    private String tipoEmpleado;

    private String categoriaCocinero;
    private String turnoCajero;

    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Long getDni() {
        return dni;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Double getSueldo() {
        return sueldo;
    }

    public void setSueldo(Double sueldo) {
        this.sueldo = sueldo;
    }

    public Long getIdUnidadVenta() {
        return idUnidadVenta;
    }

    public void setIdUnidadVenta(Long idUnidadVenta) {
        this.idUnidadVenta = idUnidadVenta;
    }

    public String getTipoEmpleado() {
        return tipoEmpleado;
    }

    public void setTipoEmpleado(String tipoEmpleado) {
        this.tipoEmpleado = tipoEmpleado;
    }

    public String getCategoriaCocinero() {
        return categoriaCocinero;
    }

    public void setCategoriaCocinero(String categoriaCocinero) {
        this.categoriaCocinero = categoriaCocinero;
    }

    public String getTurnoCajero() {
        return turnoCajero;
    }

    public void setTurnoCajero(String turnoCajero) {
        this.turnoCajero = turnoCajero;
    }
}