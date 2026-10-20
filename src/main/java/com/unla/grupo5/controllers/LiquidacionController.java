package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.LiquidacionDTO;
import com.unla.grupo5.services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/liquidaciones")
public class LiquidacionController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping
    public String verLiquidaciones(Model model) {
        List<LiquidacionDTO> liquidaciones = empleadoService.calcularLiquidaciones();
        model.addAttribute("liquidaciones", liquidaciones);
        return "liquidaciones/index"; // O el nombre de tu vista html
    }
}