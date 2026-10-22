package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.ReporteVentaDTO;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.repositories.FestivalRepository;
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
