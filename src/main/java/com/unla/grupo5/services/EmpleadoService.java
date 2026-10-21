package com.unla.grupo5.services;

import com.unla.grupo5.dtos.EmpleadoRequestDTO;
import com.unla.grupo5.dtos.LiquidacionDTO;
import com.unla.grupo5.dtos.LiquidacionHaberesDTO;
import com.unla.grupo5.entities.Staff;

import java.util.List;

public interface EmpleadoService {
    Staff registrarEmpleado(EmpleadoRequestDTO dto);

    Staff buscarPorId(Long id);

    Staff modificarEmpleado(Long id, EmpleadoRequestDTO dto);

    List<Staff> traerTodos();

    LiquidacionHaberesDTO calcularHaberes(Long idEmpleado, Long idFestival);

    void darDeBajaEmpleado(Long id);
}