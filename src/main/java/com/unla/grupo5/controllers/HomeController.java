package com.unla.grupo5.controllers;

import com.unla.grupo5.entities.Festival;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.repositories.FestivalRepository;
import com.unla.grupo5.repositories.UnidadDeVentaRepository;
import com.unla.grupo5.services.UnidadDeVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final FestivalRepository festivalRepository;
    private final UnidadDeVentaRepository unidadDeVentaRepository;

    @Autowired
    private final UnidadDeVentaService unidadDeVentaService;

    public HomeController(FestivalRepository festivalRepository, UnidadDeVentaRepository unidadDeVentaRepository, UnidadDeVentaService unidadDeVentaService) {
        this.festivalRepository = festivalRepository;
        this.unidadDeVentaRepository = unidadDeVentaRepository;
        this.unidadDeVentaService = unidadDeVentaService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("festivales", festivalRepository.findAll());
        return "index";
    }

    @GetMapping("/festivales/{id}/unidades")
    public String verUnidadesDeVenta(@PathVariable("id") Long id, @RequestParam(name = "orden", defaultValue = "desc") String orden, Model model){
        Festival festival = festivalRepository.findById(id).orElse(null);

        List<UnidadDeVenta> unidades = unidadDeVentaService.listarPorFestivalOrdenadoPorRecaudacion(id, orden);

        model.addAttribute("festival", festival);
        model.addAttribute("unidades", unidades);
        model.addAttribute("ordenActual", orden);

        return "unidades-listado";

    }
}