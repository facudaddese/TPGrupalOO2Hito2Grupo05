package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.UnidadDeVentaModificarDTO;
import com.unla.grupo5.dtos.UnidadDeVentaRequestDTO;
import com.unla.grupo5.entities.Festival;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.repositories.FestivalRepository;
import com.unla.grupo5.services.UnidadDeVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UnidadDeVentaController {

    @Autowired
    private UnidadDeVentaService unidadDeVentaService;

    @Autowired
    private FestivalRepository festivalRepository;

    // Unidades de venta del festival elegido en el inicio
    @GetMapping("/unidades-venta")
    public String listar(@RequestParam(required = false) Long idFestival, Model model) {
        Festival festival = idFestival == null ? null : festivalRepository.findById(idFestival).orElse(null);
        if (festival == null) {
            return "redirect:/";
        }

        model.addAttribute("festival", festival);
        model.addAttribute("idFestival", idFestival);
        model.addAttribute("unidades", unidadDeVentaService.listarPorFestival(idFestival));
        return "unidades-venta/listado";
    }

    @GetMapping("/alta")
    public String mostrarFormularioAlta(@RequestParam(required = false) Long idFestival, Model model) {
        UnidadDeVentaRequestDTO dto = new UnidadDeVentaRequestDTO();
        dto.setIdFestival(idFestival);
        model.addAttribute("unidadDTO", dto);
        model.addAttribute("festivales", festivalRepository.findAll());
        return "unidades-venta/form-unidad";
    }

    @PostMapping("/guardar")
    public String guardarUnidad(
            @Valid @ModelAttribute("unidadDTO") UnidadDeVentaRequestDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        if (result.hasErrors()) {
            model.addAttribute("festivales", festivalRepository.findAll());
            return "unidades-venta/form-unidad";
        }

        try {
            unidadDeVentaService.registrarUnidad(dto);
            redirect.addFlashAttribute("mensajeExito", "Unidad de venta registrada correctamente. Se han enviado las credenciales por e-mail.");
            redirect.addAttribute("idFestival", dto.getIdFestival());
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("festivales", festivalRepository.findAll());
            return "unidades-venta/form-unidad";
        }
    }

    // Modificación de una unidad de venta
    @GetMapping("/unidades-venta/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        UnidadDeVenta unidad = unidadDeVentaService.buscarPorId(id);

        // Se precarga el formulario con los datos actuales de la unidad
        UnidadDeVentaModificarDTO dto = new UnidadDeVentaModificarDTO();
        dto.setNombreComercial(unidad.getNombreComercial());
        dto.setCodigo(unidad.getCodigo());
        dto.setSuperficie(unidad.getSuperficie());

        model.addAttribute("unidadDTO", dto);
        model.addAttribute("unidadId", id);
        model.addAttribute("idFestival", unidad.getFestival().getId());
        return "unidades-venta/form-editar-unidad";
    }

    @PostMapping("/unidades-venta/editar/{id}")
    public String modificarUnidad(@PathVariable("id") Long id,
                                  @Valid @ModelAttribute("unidadDTO") UnidadDeVentaModificarDTO dto,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirect) {

        Long idFestival = unidadDeVentaService.buscarPorId(id).getFestival().getId();

        if (result.hasErrors()) {
            model.addAttribute("unidadId", id);
            model.addAttribute("idFestival", idFestival);
            return "unidades-venta/form-editar-unidad";
        }

        try {
            unidadDeVentaService.modificarUnidad(id, dto);
            redirect.addFlashAttribute("mensajeExito", "Unidad de venta modificada correctamente.");
            redirect.addAttribute("idFestival", idFestival);
            return "redirect:/unidades-venta";

        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("unidadId", id);
            model.addAttribute("idFestival", idFestival);
            return "unidades-venta/form-editar-unidad";
        }
    }
}
