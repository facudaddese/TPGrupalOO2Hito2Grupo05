package com.unla.grupo5.services;

import com.unla.grupo5.dtos.*;
import com.unla.grupo5.entities.Festival;
import com.unla.grupo5.entities.UnidadDeVenta;

import java.time.LocalDate;
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

    RentabilidadNetaDTO calcularRentabilidadNeta(UnidadDeVenta unidad, Festival festival);

    double calcularTotalSueldos(UnidadDeVenta unidad, Festival festival);

    // Todas las unidades del festival ordenadas de mayor a menor canon
    List<ReporteMayoresCanonDTO> obtenerUnidadesConMayorCanon(Long idFestival);

    // Detalle del canon que paga la unidad del responsable logueado
    CanonDTO calcularCanonUnidad(String nombreUsuario);

    double calcularTotalSueldosEntreFechas(UnidadDeVenta unidad, Festival festival, LocalDate fechaDesde, LocalDate fechaHasta);

    RentabilidadNetaDTO calcularRentabilidadNetaEntreFechas(UnidadDeVenta unidad, Festival festival, LocalDate fechaDesde, LocalDate fechaHasta);

    List<UnidadDeVenta> listarPorFestivalOrdenadoPorCantidadPedidos(Long idFestival);

    // Gestiones de Caja para la acción del Cajero / Responsable
    void cerrarCaja(Long idUnidad);

    void abrirCaja(Long idUnidad);
}