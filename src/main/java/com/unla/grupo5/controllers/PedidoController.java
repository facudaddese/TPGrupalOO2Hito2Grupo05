package com.unla.grupo5.controllers;

import com.unla.grupo5.dtos.ItemPedidoRequestDTO;
import com.unla.grupo5.dtos.PedidoRequestDTO;
import com.unla.grupo5.entities.Pedido;
import com.unla.grupo5.entities.Plato;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.services.PedidoService;
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
import java.util.List;

// El responsable registra pedidos solo para SU unidad de venta
@Controller
@RequestMapping("/responsable/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PlatoService platoService;

    @Autowired
    private UnidadDeVentaService unidadDeVentaService;

    // Lista de pedidos de la unidad del responsable
    @GetMapping
    public String listarPedidos(Principal principal, Model model) {
        UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
        model.addAttribute("pedidos", pedidoService.traerPorUnidad(unidad.getId()));
        model.addAttribute("nombreUnidad", unidad.getNombreComercial());
        return "pedidos/listado";
    }

    // Alta de un pedido
    @GetMapping("/nuevo")
    public String mostrarFormularioAlta(Principal principal, Model model) {
        UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
        List<Plato> platos = platoService.traerPorUnidad(unidad.getId());

        // Una fila por cada plato de la unidad, con cantidad 0
        PedidoRequestDTO dto = new PedidoRequestDTO();
        for (Plato plato : platos) {
            ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
            item.setIdPlato(plato.getId());
            dto.getItems().add(item);
        }

        model.addAttribute("pedidoDTO", dto);
        model.addAttribute("platos", platos);
        model.addAttribute("nombreUnidad", unidad.getNombreComercial());
        return "pedidos/form-alta-pedido";
    }

    @PostMapping("/guardar")
    public String guardarPedido(
            @Valid @ModelAttribute("pedidoDTO") PedidoRequestDTO dto,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes redirect) {

        UnidadDeVenta unidad = unidadDeVentaService.getUdVByUser(principal.getName());
        model.addAttribute("platos", platoService.traerPorUnidad(unidad.getId()));
        model.addAttribute("nombreUnidad", unidad.getNombreComercial());

        if (result.hasErrors()) {
            return "pedidos/form-alta-pedido";
        }

        try {
            // La unidad siempre es la del responsable logueado
            dto.setIdUnidadVenta(unidad.getId());
            Pedido pedido = pedidoService.registrarPedido(dto);
            redirect.addFlashAttribute("mensajeExito",
                    "Pedido N° " + pedido.getId() + " registrado correctamente. Total: $" + String.format("%.2f", pedido.getTotal()));
            return "redirect:/responsable/pedidos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            return "pedidos/form-alta-pedido";
        }
    }
}
