package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.EmpleadoRequestDTO;
import com.unla.grupo5.entities.Cajero;
import com.unla.grupo5.entities.Cocinero;
import com.unla.grupo5.entities.Staff;
import com.unla.grupo5.repositories.UnidadDeVentaRepository;
import com.unla.grupo5.services.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private UnidadDeVentaRepository unidadDeVentaRepository;

    private void cargarAtributosFormulario(Model model) {
        model.addAttribute("unidadesVenta", unidadDeVentaRepository.findAll());
        model.addAttribute("categoriasCocinero", List.of("COCINERO", "AYUDANTE", "LAVAPLATOS"));
        model.addAttribute("turnosCajero", List.of("MAÑANA", "TARDE", "NOCHE"));
    }

    // Alta de un empleado
    @GetMapping("/nuevo")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("empleadoDTO", new EmpleadoRequestDTO());
        cargarAtributosFormulario(model);
        return "empleados/form-alta-empleado";
    }

    @PostMapping("/guardar")
    public String guardarEmpleado(
            @Valid @ModelAttribute("empleadoDTO") EmpleadoRequestDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        if (result.hasErrors()) {
            cargarAtributosFormulario(model);
            return "empleados/form-alta-empleado";
        }

        try {
            empleadoService.registrarEmpleado(dto);
            redirect.addFlashAttribute("mensajeExito", "Empleado registrado correctamente. Se han enviado las credenciales por e-mail.");
            return "redirect:/empleados";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            cargarAtributosFormulario(model);
            return "empleados/form-alta-empleado";
        }
    }

    // Lista de empleados
    @GetMapping
    public String listarEmpleados(Model model) {
        model.addAttribute("empleados", empleadoService.traerTodos());
        return "empleados/listado";
    }

    // Modificar empleados
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Staff empleado = empleadoService.buscarPorId(id);

        EmpleadoRequestDTO dto = new EmpleadoRequestDTO();
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellido());
        dto.setDni(empleado.getDni());
        dto.setFechaNacimiento(empleado.getFechaNacimiento());
        dto.setFechaIngreso(empleado.getFechaIngreso());
        dto.setSueldo(empleado.getSueldo());

        if (empleado.getUsuario() != null) {
            dto.setEmail(empleado.getUsuario().getNombreUsuario());
        }

        if (empleado.getUnidadDeVenta() != null) {
            dto.setIdUnidadVenta(empleado.getUnidadDeVenta().getId());
        }

        if (empleado instanceof Cocinero) {
            Cocinero c = (Cocinero) empleado;
            dto.setTipoEmpleado("COCINERO");
            dto.setCategoriaCocinero(c.getCategoria());
        } else if (empleado instanceof Cajero) {
            Cajero caj = (Cajero) empleado;
            dto.setTipoEmpleado("CAJERO");
            dto.setTurnoCajero(caj.getTurnoTrabajo());
        }

        model.addAttribute("empleadoDTO", dto);
        model.addAttribute("empleadoId", id);
        cargarAtributosFormulario(model);
        return "empleados/form-editar-empleado";
    }

    @PostMapping("/editar/{id}")
    public String modificarEmpleado(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute("empleadoDTO") EmpleadoRequestDTO dto,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {

        if (result.hasErrors()) {
            model.addAttribute("empleadoId", id);
            cargarAtributosFormulario(model);
            return "empleados/form-editar-empleado";
        }

        try {
            empleadoService.modificarEmpleado(id, dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Empleado modificado correctamente.");
            return "redirect:/empleados";
        } catch (Exception e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("empleadoId", id);
            cargarAtributosFormulario(model);
            return "empleados/form-editar-empleado";
        }
    }
}