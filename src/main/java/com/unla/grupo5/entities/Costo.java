package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "costo")
@Getter
@Setter
@NoArgsConstructor
public class Costo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "costo_superficie", nullable = false)
    private double costoSuperficies;

    @Column(name = "costo_montaje", nullable = false)
    private double costoMontaje;

    @Column(name = "plus_electricidad", nullable = false)
    private double plusElectricidad;

    @Column(name = "sueldo_base", nullable = false)
    private double sueldoBase;

    @Column(name = "antiguedad_cajero", nullable = false)
    private Integer antiguedadCajero;

    @Column(name = "plus_cocinero", nullable = false)
    private double plusCocinero;

    @Column(name = "plus_ayudante", nullable = false)
    private double plusAyudante;

    @Column(name = "plus_lavaplatos", nullable = false)
    private double plusLavaplatos;
}