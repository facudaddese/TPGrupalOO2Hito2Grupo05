package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.EmpleadoRequestDTO;
import com.unla.grupo5.dtos.LiquidacionHaberesDTO;
import com.unla.grupo5.entities.*;
import com.unla.grupo5.exceptions.RecursoNoEncontradoException;
import com.unla.grupo5.exceptions.UnidadDeVentaNoAsignadaException;
import com.unla.grupo5.repositories.*;
import com.unla.grupo5.services.EmpleadoService;
import com.unla.grupo5.services.UnidadDeVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/responsable/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private UnidadDeVentaRepository unidadDeVentaRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UnidadDeVentaService unidadDeVentaService;

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
            return "redirect:/responsable/empleados";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            cargarAtributosFormulario(model);
            return "empleados/form-alta-empleado";
        }
    }

    // Lista de empleados de la unidad del responsable logueado
    @GetMapping
    public String listarEmpleados(Principal principal, Model model) {
        try {
            UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
            model.addAttribute("empleados", empleadoService.traerPorUnidad(unidad.getId()));
            model.addAttribute("nombreUnidad", unidad.getNombreComercial());
        } catch (IllegalArgumentException e) {
            // El responsable no tiene unidad asignada
            model.addAttribute("empleados", List.of());
            model.addAttribute("mensajeError", e.getMessage());
        }
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
            return "redirect:/responsable/empleados";
        } catch (Exception e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("empleadoId", id);
            cargarAtributosFormulario(model);
            return "empleados/form-editar-empleado";
        }
    }

    // Baja lógica de empleado
    @PostMapping("/baja/{id}")
    public String darDeBajaEmpleado(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            empleadoService.darDeBajaEmpleado(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Empleado dado de baja correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al dar de baja al empleado: " + e.getMessage());
        }
        return "redirect:/responsable/empleados";
    }

    @GetMapping("/haberes")
    public String mostrarCalculoHaberes(Model model, Authentication authentication) {

        try {
            String nombreUsuario = authentication.getName();
            Usuario responsable = usuarioRepository.findByNombreUsuario(nombreUsuario);
            UnidadDeVenta unidad = unidadDeVentaRepository.findByResponsable(responsable).orElseThrow(UnidadDeVentaNoAsignadaException::new);

            Festival festival = unidad.getFestival();

            List<Staff> empleados = staffRepository.findByUnidadDeVentaAndActivoTrue(unidad);

            List<LiquidacionHaberesDTO> haberes = new ArrayList<>();

            for (Staff empleado : empleados) {
                LiquidacionHaberesDTO liquidacion = empleadoService.calcularHaberes(empleado.getId(), festival.getId());
                haberes.add(liquidacion);
            }

            long diasFestival = ChronoUnit.DAYS.between(festival.getFechaInicio(), festival.getFechaFin()) + 1;

            model.addAttribute("diasFestival", diasFestival);
            model.addAttribute("haberes", haberes);
            model.addAttribute("unidad", unidad);
            model.addAttribute("festival", festival);

        } catch (UnidadDeVentaNoAsignadaException e) {
            model.addAttribute("mensajeError", e.getMessage());

        } catch (RecursoNoEncontradoException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }

        return "empleados/calculo-haberes";
    }

}