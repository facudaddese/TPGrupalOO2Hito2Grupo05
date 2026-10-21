package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.LiquidacionHaberesDTO;
import com.unla.grupo5.entities.Festival;
import com.unla.grupo5.entities.Staff;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.entities.Usuario;
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

    @GetMapping("/sueldos")
    public String consultarSueldos(
            Model model,
            Authentication authentication) {

        String nombreUsuario = authentication.getName();

        Usuario usuario =
                usuarioRepository.findByNombreUsuario(nombreUsuario);

        Staff empleado = usuario.getStaff();

        if (empleado == null) {
            model.addAttribute(
                    "mensajeError",
                    "El usuario no tiene un empleado asociado."
            );

            return "empleados/mis-sueldos";
        }

        UnidadDeVenta unidad = empleado.getUnidadDeVenta();
        Festival festival = unidad.getFestival();

        LiquidacionHaberesDTO liquidacion =
                empleadoService.calcularHaberes(
                        empleado.getId(),
                        festival.getId()
                );

        model.addAttribute("empleado", empleado);
        model.addAttribute("unidad", unidad);
        model.addAttribute("festival", festival);
        model.addAttribute("liquidacion", liquidacion);

        return "rol_empleado/mis-sueldos";
    }
}
