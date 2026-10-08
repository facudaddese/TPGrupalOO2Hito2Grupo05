package com.unla.grupo5.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "unidadDeVenta")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class UnidadDeVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombreComercial", nullable = false)
    private String nombreComercial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idStaffResponsable", nullable = true)
    private Staff responsable;

    @Column(name = "superficie", nullable = false)
    private int superficie;

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_festival", nullable = false)
    private Festival festival;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUnidadVenta")
    private Set<Plato> lstPlatos = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUnidadVenta")
    private Set<Staff> lstStaff = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUnidadDeVenta")
    private Set<Pedido> lstPedidos = new HashSet<>();
}
