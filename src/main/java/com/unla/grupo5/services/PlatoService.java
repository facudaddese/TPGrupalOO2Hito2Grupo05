package com.unla.grupo5.services;

import com.unla.grupo5.dtos.PlatoDTO;
import com.unla.grupo5.dtos.PlatoModificarDTO;
import com.unla.grupo5.dtos.PlatoRequestDTO;
import com.unla.grupo5.entities.Plato;

import java.util.List;

public interface PlatoService {
    Plato registrarPlato(PlatoRequestDTO dto);

    Plato buscarPorId(Long id);

    Plato modificarPlato(Long id, PlatoModificarDTO dto);

    List<Plato> traerPorUnidad(Long idUnidadVenta);

    // Plato de la unidad que registró más pedidos en el festival (null si no hubo pedidos)
    PlatoDTO obtenerPlatoEstrella(Long idUnidadVenta, Long idFestival);
}
