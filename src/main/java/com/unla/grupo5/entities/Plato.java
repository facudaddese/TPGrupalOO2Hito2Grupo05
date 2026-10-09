package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plato")
@Getter
@Setter
@NoArgsConstructor
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private double precio;

    @Column(name = "costo_produccion", nullable = false)
    private double costoProduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_de_venta", nullable = false)
    private UnidadDeVenta unidadDeVenta;
}