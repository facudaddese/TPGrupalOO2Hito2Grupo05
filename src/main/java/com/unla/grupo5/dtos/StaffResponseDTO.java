package com.unla.grupo5.dtos;

import com.unla.grupo5.entities.Cajero;
import com.unla.grupo5.entities.Cocinero;
import com.unla.grupo5.entities.Staff;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private Long dni;
    private String email;
    private LocalDate fechaNacimiento;
    private LocalDate fechaIngreso;
    private boolean activo;
    private String tipoEmpleado;
    private String categoriaCocinero;
    private String turnoCajero;
    private Long idUnidadDeVenta;
    private String nombreUnidadDeVenta;

    public static StaffResponseDTO fromEntity(Staff staff) {
        StaffResponseDTO dto = new StaffResponseDTO();
        dto.setId(staff.getId());
        dto.setNombre(staff.getNombre());
        dto.setApellido(staff.getApellido());
        dto.setDni(staff.getDni());
        dto.setFechaNacimiento(staff.getFechaNacimiento());
        dto.setFechaIngreso(staff.getFechaIngreso());
        dto.setActivo(staff.isActivo());

        if (staff.getUsuario() != null) {
            dto.setEmail(staff.getUsuario().getNombreUsuario());
        }

        if (staff instanceof Cocinero cocinero) {
            dto.setTipoEmpleado("COCINERO");
            dto.setCategoriaCocinero(cocinero.getCategoria());
        } else if (staff instanceof Cajero cajero) {
            dto.setTipoEmpleado("CAJERO");
            dto.setTurnoCajero(cajero.getTurnoTrabajo());
        }

        if (staff.getUnidadDeVenta() != null) {
            dto.setIdUnidadDeVenta(staff.getUnidadDeVenta().getId());
            dto.setNombreUnidadDeVenta(staff.getUnidadDeVenta().getNombreComercial());
        }

        return dto;
    }
}