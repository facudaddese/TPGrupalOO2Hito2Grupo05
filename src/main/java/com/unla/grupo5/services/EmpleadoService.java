package com.unla.grupo5.services;

import com.unla.grupo5.dtos.EmpleadoRequestDTO;
import com.unla.grupo5.entities.Staff;

import java.util.List;

public interface EmpleadoService {
    Staff registrarEmpleado(EmpleadoRequestDTO dto);

    Staff buscarPorId(Long id);

    Staff modificarEmpleado(Long id, EmpleadoRequestDTO dto);

    List<Staff> traerTodos();
}