package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.LiquidacionHaberesDTO;
import com.unla.grupo5.entities.*;
import com.unla.grupo5.exceptions.RecursoNoEncontradoException;
import com.unla.grupo5.repositories.StaffRepository;
import com.unla.grupo5.repositories.UsuarioRepository;
import com.unla.grupo5.services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/empleado")
public class Rol_EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private StaffRepository staffRepository;

    @GetMapping("/sueldos")
    public String consultarSueldos(Model model, Authentication authentication) {

        try{
            String nombreUsuario = authentication.getName();

            Staff empleado = staffRepository.findByUsuarioNombreUsuario(nombreUsuario).orElseThrow(() ->
                             new RecursoNoEncontradoException("Empleado"));

            UnidadDeVenta unidad = empleado.getUnidadDeVenta();
            Festival festival = unidad.getFestival();

            LiquidacionHaberesDTO liquidacion = empleadoService.calcularHaberes(empleado.getId(), festival.getId());

            model.addAttribute("empleado", empleado);
            model.addAttribute("unidad", unidad);
            model.addAttribute("festival", festival);
            model.addAttribute("liquidacion", liquidacion);

        } catch (RecursoNoEncontradoException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }

        return "rol_empleado/mis-sueldos";
    }
}
