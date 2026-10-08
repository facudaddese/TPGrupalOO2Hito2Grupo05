package com.unla.grupo5.controllers;

import com.unla.grupo5.repositories.FestivalRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final FestivalRepository festivalRepository;

    public HomeController(FestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("festivales", festivalRepository.findAll());
        return "index";
    }
}