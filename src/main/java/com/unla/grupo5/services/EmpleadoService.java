package com.unla.grupo5.services;

import com.unla.grupo5.dtos.EmpleadoRequestDTO;
import com.unla.grupo5.dtos.LiquidacionHaberesDTO;
import com.unla.grupo5.entities.Staff;

import java.util.List;

public interface EmpleadoService {
    Staff registrarEmpleado(EmpleadoRequestDTO dto);

    Staff buscarPorId(Long id);

    Staff modificarEmpleado(Long id, EmpleadoRequestDTO dto);

    List<Staff> traerTodos();

    List<Staff> traerPorUnidad(Long idUnidadVenta);

    public List<LiquidacionHaberesDTO> calcularHaberesMensuales(Long idEmpleado, Long idFestival);

    void darDeBajaEmpleado(Long id);

    public List<LiquidacionHaberesDTO> filtrarMesesCerrados(List<LiquidacionHaberesDTO> liquidaciones);

    void reactivarEmpleado(Long id);

    List<Staff> traerActivosPorUnidad(Long idUnidadVenta);
}