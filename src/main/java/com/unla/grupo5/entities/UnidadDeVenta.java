package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_staff_responsable", nullable = true)
    private Staff responsable;

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
}