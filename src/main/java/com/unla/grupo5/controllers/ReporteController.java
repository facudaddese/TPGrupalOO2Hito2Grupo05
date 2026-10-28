package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.ReporteVentaDTO;
import com.unla.grupo5.entities.Festival;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.repositories.FestivalRepository;
import com.unla.grupo5.services.PlatoService;
import com.unla.grupo5.services.UnidadDeVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/responsable/reportes")
public class ReporteController {

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private UnidadDeVentaService unidadDeVentaService;

    @Autowired
    private PlatoService platoService;

    // Plato estrella de la unidad del responsable en el festival al que pertenece
    @GetMapping("/plato-estrella")
    public String mostrarPlatoEstrella(Model model, Principal principal) {
        try {
            UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
            Festival festival = unidad.getFestival();
            if (festival == null) {
                throw new IllegalArgumentException("Tu unidad de venta no tiene un festival asignado.");
            }

            model.addAttribute("unidad", unidad);
            model.addAttribute("festival", festival);
            model.addAttribute("platoEstrella", platoService.obtenerPlatoEstrella(unidad.getId(), festival.getId()));
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }
        return "reportes/plato-estrella";
    }

    @GetMapping("/recaudacion")
    public String mostrarFormularioReporte(Model model, Principal principal){
        try {
            UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
            model.addAttribute("unidad", unidad);
        }catch (Exception e){
            model.addAttribute("festivales", festivalRepository.findAll());
        }
        model.addAttribute("festivales", festivalRepository.findAll());
        return "reportes/recaudacion";
    }

    @PostMapping("/recaudacion/generar")
    public String generarReporteRecaudacion(@RequestParam("idFestival") Long idFestival, Model model, Principal principal){
        try{
            UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
            ReporteVentaDTO reporte = unidadDeVentaService.generarReporteRecaudacion(idFestival, principal.getName());

            model.addAttribute("reporte", reporte);
            model.addAttribute("unidad", unidad);
            model.addAttribute("festivalSeleccionado", idFestival);
        } catch (Exception e){
            model.addAttribute("mensajeError", e.getMessage());
        }

        model.addAttribute("festivales", festivalRepository.findAll());
        return "reportes/recaudacion";
    }

}
