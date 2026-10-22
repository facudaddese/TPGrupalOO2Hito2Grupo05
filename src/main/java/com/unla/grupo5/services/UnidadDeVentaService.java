package com.unla.grupo5.services;

import com.unla.grupo5.dtos.ReporteVentaDTO;
import com.unla.grupo5.dtos.UnidadDeVentaModificarDTO;
import com.unla.grupo5.dtos.UnidadDeVentaRequestDTO;
import com.unla.grupo5.entities.UnidadDeVenta;

import java.util.List;

public interface UnidadDeVentaService {
    List<UnidadDeVenta> listarPorFestival(Long idFestival);

    UnidadDeVenta registrarUnidad(UnidadDeVentaRequestDTO dto);

    UnidadDeVenta buscarPorId(Long id);

    UnidadDeVenta modificarUnidad(Long id, UnidadDeVentaModificarDTO dto);

    // Unidad de venta a la que pertenece el usuario (el responsable) logueado
    UnidadDeVenta getUdVByUser(String nombreUsuario);

    List<UnidadDeVenta> listarPorFestivalOrdenadoPorRecaudacion(Long idFestival, String orden);

    ReporteVentaDTO generarReporteRecaudacion(Long idFestival, String nombreUsuario);
}
