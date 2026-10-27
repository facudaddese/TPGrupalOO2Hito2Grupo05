package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.RentabilidadNetaDTO;
import com.unla.grupo5.entities.Festival;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.entities.Usuario;
import com.unla.grupo5.repositories.FestivalRepository;
import com.unla.grupo5.repositories.UnidadDeVentaRepository;
import com.unla.grupo5.repositories.UsuarioRepository;
import com.unla.grupo5.services.UnidadDeVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/responsable/rentabilidad")
public class RentabilidadController {
    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private UnidadDeVentaService unidadDeVentaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UnidadDeVentaRepository unidadDeVentaRepository;

    @GetMapping
    public String mostrarRentabilidad(Authentication authentication, Model model) {

        Usuario responsable = usuarioRepository.findByNombreUsuario(authentication.getName());

        if (responsable == null) {
            model.addAttribute("mensajeError", "No se pudo identificar al usuario autenticado.");
            return "reportes/rentabilidad";
        }

        UnidadDeVenta unidad = unidadDeVentaRepository.findByResponsable(responsable).orElse(null);

        if (unidad == null) {
            model.addAttribute("mensajeError", "No tenés una Unidad de Venta asignada.");
            return "reportes/rentabilidad";
        }

        Festival festival = unidad.getFestival();

        if (festival == null) {
            model.addAttribute("mensajeError", "Tu Unidad de Venta no tiene un festival asignado.");
            return "reportes/rentabilidad";
        }

        if (festival.getFechaInicio() == null || festival.getFechaFin() == null || festival.getFechaFin().isBefore(festival.getFechaInicio())) {
            model.addAttribute("mensajeError", "El festival no tiene un período válido configurado.");
            return "reportes/rentabilidad";
        }

        RentabilidadNetaDTO rentabilidad = unidadDeVentaService.calcularRentabilidadNeta(unidad, festival);

        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String estadoFestival;

        if (hoy.isBefore(festival.getFechaInicio())) {
            estadoFestival = "Próximo";
        } else if (hoy.isAfter(festival.getFechaFin())) {
            estadoFestival = "Finalizado";
        } else {
            estadoFestival = "En curso";
        }

        model.addAttribute("unidad", unidad);
        model.addAttribute("festival", festival);
        model.addAttribute("rentabilidad", rentabilidad);
        model.addAttribute("fechaConsulta", hoy.format(formato));
        model.addAttribute("fechaInicio", festival.getFechaInicio().format(formato));
        model.addAttribute("fechaFin", festival.getFechaFin().format(formato));
        model.addAttribute("estadoFestival", estadoFestival);

        return "reportes/rentabilidad";
    }
}
