package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "unidad_de_venta")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class UnidadDeVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre_comercial", nullable = false)
    private String nombreComercial;

    // Un solo usuario es el responsable de la unidad
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_responsable", nullable = true)
    private Usuario responsable;

    @Column(name = "superficie", nullable = false)
    private Integer superficie;

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_festival", nullable = false)
    private Festival festival;

    @OneToMany(mappedBy = "unidadDeVenta", fetch = FetchType.LAZY)
    private Set<Plato> lstPlatos = new HashSet<>();

    @OneToMany(mappedBy = "unidadDeVenta", fetch = FetchType.LAZY)
    private Set<Staff> lstStaff = new HashSet<>();

    @OneToMany(mappedBy = "unidadDeVenta", fetch = FetchType.LAZY)
    private Set<Pedido> lstPedidos = new HashSet<>();

    // Estado de la caja para el CU "Cerrar Caja"
    @Column(name = "caja_abierta", nullable = false)
    private boolean cajaAbierta = true;

    // Campo transitorio para almacenar la cantidad calculada de pedidos en los rankings.
    // @Transient evita la persistencia en la BD ya que no existe una columna física asociada.
    @Transient
    private Long cantidadPedidos;


    public double getRecaudacionTotal() {
        if (lstPedidos == null || lstPedidos.isEmpty()) {
            return 0.0;
        }
        return lstPedidos.stream().mapToDouble(Pedido::getTotal).sum();
    }

    public double getCostoTotal() {
        double total = 0.0;

        if (lstPedidos != null && !lstPedidos.isEmpty()) {

            for (Pedido pedido : lstPedidos) {
                if (pedido.getListaItems() != null) {

                    for (ItemPedido ip : pedido.getListaItems()) {
                        total += ip.getPlato().getCostoProduccion() * ip.getCantidad();
                    }
                }
            }
        }

        return total;
    }

    public double getRecaudacionEntreFechas(LocalDate inicio, LocalDate fin) {
        double total = 0.0;

        if (lstPedidos != null && !lstPedidos.isEmpty()) {
            for (Pedido pedido : lstPedidos) {
                if (!pedido.getFecha().isBefore(inicio) && !pedido.getFecha().isAfter(fin)) {
                    total += pedido.getTotal();
                }
            }
        }

        return total;
    }

    public double getCostoTotalEntreFechas(LocalDate inicio, LocalDate fin) {
        double total = 0.0;
        if (lstPedidos != null && !lstPedidos.isEmpty()) {

            for (Pedido pedido : lstPedidos) {
                if (!pedido.getFecha().isBefore(inicio) && !pedido.getFecha().isAfter(fin)) {
                    for (ItemPedido ip : pedido.getListaItems()) {
                        total += ip.getPlato().getCostoProduccion() * ip.getCantidad();
                    }

                }
            }
        }

        return total;
    }
}