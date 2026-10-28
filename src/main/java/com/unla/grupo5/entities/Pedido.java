package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<ItemPedido> listaItems = new HashSet<>();

    @Column(name = "total", nullable = false)
    private double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_de_venta", nullable = false)
    private UnidadDeVenta unidadDeVenta;

    public String getDetallePlatos() {
        if (listaItems == null || listaItems.isEmpty()) {
            return "Sin ítems";
        }
        StringBuilder sb = new StringBuilder();
        for (ItemPedido item : listaItems) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(item.getCantidad())
                    .append("x ")
                    .append(item.getPlato() != null ? item.getPlato().getNombre() : "Plato");
        }
        return sb.toString();
    }
}