package com.unla.grupo5.controllers;

import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.services.PedidoService;
import com.unla.grupo5.services.UnidadDeVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/cajero/pedidos")
public class CajeroPedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UnidadDeVentaService unidadDeVentaService;

    @GetMapping
    public String listarPedidos(Principal principal, Model model) {
        UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());

        model.addAttribute("pedidos", pedidoService.traerPorUnidadConItems(unidad.getId()));
        model.addAttribute("nombreUnidad", unidad.getNombreComercial());
        model.addAttribute("cajaAbierta", unidad.isCajaAbierta());

        return "pedidos/listado";
    }

    @PostMapping("/cerrar-caja")
    public String cerrarCaja(Principal principal, RedirectAttributes redirect) {
        try {
            UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
            unidadDeVentaService.cerrarCaja(unidad.getId());
            redirect.addFlashAttribute("mensajeExito", "La caja ha sido cerrada correctamente al finalizar la jornada.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/cajero/pedidos";
    }

    @PostMapping("/abrir-caja")
    public String abrirCaja(Principal principal, RedirectAttributes redirect) {
        try {
            UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
            unidadDeVentaService.abrirCaja(unidad.getId());
            redirect.addFlashAttribute("mensajeExito", "La caja ha sido abierta correctamente.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/cajero/pedidos";
    }
}