package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plato")
@Getter
@Setter
@NoArgsConstructor
public class Plato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlato;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Long precio;

    @Column(name = "costo_produccion", nullable = false)
    private Long costoProduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUnidadDeVenta", nullable = false)
    private UnidadDeVenta unidadDeVenta;

    @OneToMany(mappedBy = "plato", fetch = FetchType.LAZY)
    private Set<ItemPedido> listaItems = new HashSet<>();

}
