package com.unla.grupo5.controllers;

import com.unla.grupo5.entities.Festival;
import com.unla.grupo5.entities.Staff;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.repositories.FestivalRepository;
import com.unla.grupo5.repositories.StaffRepository;
import com.unla.grupo5.repositories.UnidadDeVentaRepository;
import com.unla.grupo5.services.UnidadDeVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    private final FestivalRepository festivalRepository;
    private final UnidadDeVentaRepository unidadDeVentaRepository;
    private final UnidadDeVentaService unidadDeVentaService;
    private final StaffRepository staffRepository;

    public HomeController(FestivalRepository festivalRepository, UnidadDeVentaRepository unidadDeVentaRepository, UnidadDeVentaService unidadDeVentaService, StaffRepository staffRepository) {
        this.festivalRepository = festivalRepository;
        this.unidadDeVentaRepository = unidadDeVentaRepository;
        this.unidadDeVentaService = unidadDeVentaService;
        this.staffRepository = staffRepository;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {

        //agregado para que el listado de festivales lo puedan ver visitantes no autenticados
        boolean autenticado = authentication != null
                && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser");

        boolean esResponsable = autenticado
                && authentication.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_RESPONSABLE")
                );

        //Validacion de si es empleado
        boolean esEmpleado = autenticado
                && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));

        if (esResponsable) {
            try {
                // Unidad de venta asignada al responsable logueado
                UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(authentication.getName());
                model.addAttribute("unidad", unidad);

                // Solo se muestra el festival al que pertenece su unidad
                Festival festival = festivalRepository.findById(unidad.getFestival().getId()).orElse(null);
                model.addAttribute("festivales", festival == null ? List.of() : List.of(festival));

            } catch (IllegalArgumentException e) {
                // El responsable todavía no tiene unidad asignada
                model.addAttribute("mensajeError", e.getMessage());
                model.addAttribute("festivales", List.of());
            }
        } else if(esEmpleado){
            Staff empleado = staffRepository.findByUsuarioNombreUsuario(authentication.getName()).orElse(null);

            if(empleado != null && empleado.getUnidadDeVenta() != null){
                UnidadDeVenta unidad = empleado.getUnidadDeVenta();
                Festival festival = unidad.getFestival();
                model.addAttribute("miUnidad", unidad);
                model.addAttribute("miFestival", festival);

                //Validacion de si debe trabajar actualmente
                LocalDate hoy = LocalDate.now();
                boolean festivalActivo = !hoy.isBefore(festival.getFechaInicio()) && !hoy.isAfter(festival.getFechaFin());
                model.addAttribute("festivalActivo", festivalActivo);
            }else{
                model.addAttribute("mensajeErrorEmpleado", "No tienes una unidad de venta ni festival asignados.");
            }
            model.addAttribute("festivales", festivalRepository.findAll());

        } else {
            model.addAttribute("festivales", festivalRepository.findAll());
        }

        return "index";
    }

    @GetMapping("/admin/festivales/{id}/unidades")
    public String verUnidadesDeVenta(@PathVariable("id") Long id,
                                     @RequestParam(name = "orden", defaultValue = "desc") String orden,
                                     @RequestParam(name = "criterio", defaultValue = "recaudacion") String criterio,
                                     Model model){
        Festival festival = festivalRepository.findById(id).orElse(null);

        List<UnidadDeVenta> unidades;

        if("pedidos".equalsIgnoreCase(criterio)){
            unidades = unidadDeVentaService.listarPorFestivalOrdenadoPorCantidadPedidos(id);
        }else{
            unidades = unidadDeVentaService.listarPorFestivalOrdenadoPorRecaudacion(id, orden);
        }

        model.addAttribute("festival", festival);
        model.addAttribute("unidades", unidades);
        model.addAttribute("ordenActual", orden);
        model.addAttribute("criterioActual", criterio);

        return "unidades-listado";

    }

}