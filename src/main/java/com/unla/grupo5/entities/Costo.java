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

    @Column(name = "costo_superficie")
    private double costoSuperficie;

    @Column(name = "costo_montaje")
    private double costoMontaje;

    @Column(name = "plus_electricidad")
    private double plusElectricidad;

    @Column(name = "sueldo_base")
    private double sueldoBase;

    @Column(name = "antiguedad_cajero")
    private int anioAntiguedadCajero;
}
