package com.unla.grupo5.controllers;

import com.unla.grupo5.services.UnidadDeVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

// El responsable consulta el canon que paga SU unidad de venta
@Controller
@RequestMapping("/responsable/canon")
public class CanonController {

    @Autowired
    private UnidadDeVentaService unidadDeVentaService;

    @GetMapping
    public String mostrarCanon(Principal principal, Model model) {
        try {
            model.addAttribute("detalle", unidadDeVentaService.calcularCanonUnidad(principal.getName()));
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }
        return "reportes/canon";
    }
}
