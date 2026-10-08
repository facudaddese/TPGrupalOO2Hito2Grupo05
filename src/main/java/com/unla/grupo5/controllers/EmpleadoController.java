package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.EmpleadoRequestDTO;
import com.unla.grupo5.repositories.CategoriaCocineroRepository;
import com.unla.grupo5.repositories.TurnoCajeroRepository;
import com.unla.grupo5.repositories.UnidadDeVentaRepository;
import com.unla.grupo5.services.IEmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private IEmpleadoService empleadoService;

    @Autowired
    private UnidadDeVentaRepository unidadDeVentaRepository;

    @Autowired
    private CategoriaCocineroRepository categoriaCocineroRepository;

    @Autowired
    private TurnoCajeroRepository turnoCajeroRepository;

    private void cargarAtributosFormulario(Model model) {
        model.addAttribute("unidadesVenta", unidadDeVentaRepository.findAll());
        model.addAttribute("categoriasCocinero", categoriaCocineroRepository.findAll());
        model.addAttribute("turnosCajero", turnoCajeroRepository.findAll());
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("empleadoDTO", new EmpleadoRequestDTO());
        cargarAtributosFormulario(model);
        return "empleados/form-empleado";
    }

    @PostMapping("/guardar")
    public String guardarEmpleado(
            @Valid @ModelAttribute("empleadoDTO") EmpleadoRequestDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        if (result.hasErrors()) {
            cargarAtributosFormulario(model);
            return "empleados/form-empleado";
        }

        try {
            empleadoService.registrarEmpleado(dto);
            redirect.addFlashAttribute("mensajeExito", "Empleado registrado correctamente. Se han enviado las credenciales por e-mail.");
            return "redirect:/empleados/nuevo";

        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            cargarAtributosFormulario(model);
            return "empleados/form-empleado";
        }
    }
}