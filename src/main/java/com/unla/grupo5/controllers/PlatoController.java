package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.PlatoModificarDTO;
import com.unla.grupo5.dtos.PlatoRequestDTO;
import com.unla.grupo5.entities.Plato;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.services.PlatoService;
import com.unla.grupo5.services.UnidadDeVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

// El responsable solo trabaja con los platos de SU unidad de venta
@Controller
@RequestMapping("/responsable/platos")
public class PlatoController {

    @Autowired
    private PlatoService platoService;

    @Autowired
    private UnidadDeVentaService unidadDeVentaService;

    // Lista de platos de la unidad del responsable
    @GetMapping
    public String listarPlatos(Principal principal, Model model) {
        UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
        model.addAttribute("platos", platoService.traerPorUnidad(unidad.getId()));
        model.addAttribute("nombreUnidad", unidad.getNombreComercial());
        return "platos/listado";
    }

    // Alta de un plato
    @GetMapping("/nuevo")
    public String mostrarFormularioAlta(Principal principal, Model model) {
        UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
        model.addAttribute("platoDTO", new PlatoRequestDTO());
        model.addAttribute("nombreUnidad", unidad.getNombreComercial());
        return "platos/form-alta-plato";
    }

    @PostMapping("/guardar")
    public String guardarPlato(
            @Valid @ModelAttribute("platoDTO") PlatoRequestDTO dto,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes redirect) {

        UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
        model.addAttribute("nombreUnidad", unidad.getNombreComercial());

        if (result.hasErrors()) {
            return "platos/form-alta-plato";
        }

        try {
            // La unidad siempre es la del responsable logueado
            dto.setIdUnidadVenta(unidad.getId());
            platoService.registrarPlato(dto);
            redirect.addFlashAttribute("mensajeExito", "Plato registrado correctamente.");
            return "redirect:/responsable/platos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            return "platos/form-alta-plato";
        }
    }

    // Modificación de un plato
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Principal principal,
                                          Model model, RedirectAttributes redirect) {
        Plato plato = platoService.buscarPorId(id);
        UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());

        if (!pertenece(plato, unidad)) {
            redirect.addFlashAttribute("mensajeError", "El plato no pertenece a tu unidad de venta.");
            return "redirect:/responsable/platos";
        }

        // Se precarga el formulario con los datos actuales del plato
        PlatoModificarDTO dto = new PlatoModificarDTO();
        dto.setNombre(plato.getNombre());
        dto.setPrecio(plato.getPrecio());
        dto.setCostoProduccion(plato.getCostoProduccion());

        model.addAttribute("platoDTO", dto);
        model.addAttribute("platoId", id);
        model.addAttribute("nombreUnidad", unidad.getNombreComercial());
        return "platos/form-editar-plato";
    }

    @PostMapping("/editar/{id}")
    public String modificarPlato(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("platoDTO") PlatoModificarDTO dto,
                                 BindingResult result,
                                 Principal principal,
                                 Model model,
                                 RedirectAttributes redirect) {

        Plato plato = platoService.buscarPorId(id);
        UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());

        if (!pertenece(plato, unidad)) {
            redirect.addFlashAttribute("mensajeError", "El plato no pertenece a tu unidad de venta.");
            return "redirect:/responsable/platos";
        }

        if (result.hasErrors()) {
            model.addAttribute("platoId", id);
            model.addAttribute("nombreUnidad", unidad.getNombreComercial());
            return "platos/form-editar-plato";
        }

        try {
            platoService.modificarPlato(id, dto);
            redirect.addFlashAttribute("mensajeExito", "Plato modificado correctamente.");
            return "redirect:/responsable/platos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("platoId", id);
            model.addAttribute("nombreUnidad", unidad.getNombreComercial());
            return "platos/form-editar-plato";
        }
    }

    // Un responsable solo puede modificar platos de su propia unidad
    private boolean pertenece(Plato plato, UnidadDeVenta unidad) {
        return plato.getUnidadDeVenta().getId().equals(unidad.getId());
    }
}
