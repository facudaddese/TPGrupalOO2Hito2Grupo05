package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.LiquidacionHaberesDTO;
import com.unla.grupo5.entities.*;
import com.unla.grupo5.exceptions.RecursoNoEncontradoException;
import com.unla.grupo5.repositories.StaffRepository;
import com.unla.grupo5.services.EmpleadoService;
import com.unla.grupo5.services.ReciboSueldoPdfService;
import org.openpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/empleado")
public class Rol_EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ReciboSueldoPdfService reciboSueldoPdfService;

    @Autowired
    private StaffRepository staffRepository;

    @GetMapping("/sueldos")
    public String consultarSueldos(Model model, Authentication authentication) {

        try{
            String nombreUsuario = authentication.getName();

            Staff empleado = staffRepository.findByUsuarioNombreUsuario(nombreUsuario).orElseThrow(() ->
                             new RecursoNoEncontradoException("Puesto de staff"));

            UnidadDeVenta unidad = empleado.getUnidadDeVenta();
            Festival festival = unidad.getFestival();

            List<LiquidacionHaberesDTO> liquidaciones =
                    empleadoService.calcularHaberesMensuales(
                            empleado.getId(),
                            festival.getId()
                    );

            model.addAttribute("empleado", empleado);
            model.addAttribute("unidad", unidad);
            model.addAttribute("festival", festival);
            model.addAttribute("liquidaciones", liquidaciones);

        } catch (RecursoNoEncontradoException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }

        return "rol_empleado/mis-sueldos";
    }


    @GetMapping("/sueldos/pdf")
    public ResponseEntity<byte[]> descargarRecibo(@RequestParam String periodo, Authentication authentication) {

        try {
            String nombreUsuario = authentication.getName();

            Staff empleado = staffRepository.findByUsuarioNombreUsuario(nombreUsuario)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Especializacion del empleado"));

            UnidadDeVenta unidad = empleado.getUnidadDeVenta();

            if (unidad == null || unidad.getFestival() == null) {
                return ResponseEntity.notFound().build();
            }

            Festival festival = unidad.getFestival();

            YearMonth mesSolicitado = YearMonth.parse(periodo);

            List<LiquidacionHaberesDTO> liquidaciones =
                    empleadoService.calcularHaberesMensuales(
                            empleado.getId(),
                            festival.getId()
                    );

            LiquidacionHaberesDTO liquidacion = liquidaciones.stream()
                    .filter(l -> YearMonth.from(l.getPeriodoInicio())
                            .equals(mesSolicitado))
                    .findFirst()
                    .orElse(null);

            if (liquidacion == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdf = reciboSueldoPdfService.generarRecibo(
                    liquidacion,
                    festival,
                    unidad
            );

            String nombreArchivo = "recibo_sueldo_" + periodo + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + nombreArchivo + "\""
                    )
                    .body(pdf);

        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.notFound().build();

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();

        } catch (DocumentException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
