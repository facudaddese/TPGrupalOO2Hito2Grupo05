package com.unla.grupo5.services;

import com.unla.grupo5.dtos.*;
import com.unla.grupo5.entities.Staff;

import java.time.LocalDate;
import java.util.List;

public interface EmpleadoService {
    Staff registrarEmpleado(EmpleadoRequestDTO dto, String usuarioResponsable);

    Staff buscarPorId(Long id);

    Staff modificarEmpleado(Long id, EmpleadoRequestDTO dto, String usuarioResponsable);

    List<Staff> traerTodos();

    List<Staff> traerPorUnidad(Long idUnidadVenta);

    public List<LiquidacionHaberesDTO> calcularHaberesMensuales(Long idEmpleado, Long idFestival);

    void darDeBajaEmpleado(Long id);

    List<LiquidacionHaberesDTO> filtrarMesesCerrados(List<LiquidacionHaberesDTO> liquidaciones);

    List<LiquidacionHaberesDTO> calcularHaberesHastaFecha(Long idEmpleado, Long idFestival, LocalDate fechaCorte);

    void reactivarEmpleado(Long id);

    List<Staff> traerActivosPorUnidad(Long idUnidadVenta);

    // Festivales y unidades de venta en las que trabajó el empleado logueado
    List<HistorialTrabajoDTO> obtenerHistorialTrabajo(String nombreUsuario);

    StaffResponseDTO registrarEmpleadoApi(StaffCreateRequestDTO dto);

    List<StaffResponseDTO> buscarEmpleadosApi(
            String nombre,
            String apellido,
            String tipoEmpleado,
            Long idUnidadDeVenta,
            Boolean activo
    );

    StaffResponseDTO obtenerEmpleadoPorIdApi(Long id);
}