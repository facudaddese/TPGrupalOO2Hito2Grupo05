package com.unla.grupo5.services;

import com.unla.grupo5.dtos.EmpleadoRequestDTO;
import com.unla.grupo5.entities.Staff;

public interface IEmpleadoService {
    Staff registrarEmpleado(EmpleadoRequestDTO dto);
}