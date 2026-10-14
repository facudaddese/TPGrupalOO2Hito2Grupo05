package com.unla.grupo5.services;

import com.unla.grupo5.dtos.PlatoModificarDTO;
import com.unla.grupo5.dtos.PlatoRequestDTO;
import com.unla.grupo5.entities.Plato;

import java.util.List;

public interface PlatoService {
    Plato registrarPlato(PlatoRequestDTO dto);

    Plato buscarPorId(Long id);

    Plato modificarPlato(Long id, PlatoModificarDTO dto);

    List<Plato> traerPorUnidad(Long idUnidadVenta);
}
