package com.unla.grupo5.controllers;

import com.unla.grupo5.entities.Festival;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.repositories.FestivalRepository;
import com.unla.grupo5.repositories.UnidadDeVentaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HomeController {

    private final FestivalRepository festivalRepository;
    private final UnidadDeVentaRepository unidadDeVentaRepository;

    public HomeController(FestivalRepository festivalRepository, UnidadDeVentaRepository unidadDeVentaRepository) {
        this.festivalRepository = festivalRepository;
        this.unidadDeVentaRepository = unidadDeVentaRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("festivales", festivalRepository.findAll());
        return "index";
    }

    @GetMapping("/festivales/{id}/unidades")
    public String verUnidadesDeVenta(@PathVariable("id") Long id, Model model){
        Festival festival = festivalRepository.findById(id).orElse(null);

        List<UnidadDeVenta> unidades = unidadDeVentaRepository.findByFestivalId(id);

        model.addAttribute("festival", festival);
        model.addAttribute("unidades", unidades);

        return "unidades-listado";

    }
}