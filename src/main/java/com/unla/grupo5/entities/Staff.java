package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "staff")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public abstract class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true)
    private int dni;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(nullable = false)
    private double sueldo;

    @Column(nullable = false)
    private boolean activo = true;

    // Muchos empleados en una sola unidad de venta
    @ManyToOne(fetch = FetchType.LAZY)
    // Clave foranea
    @JoinColumn(name = "idUnidadVenta")
    private UnidadDeVenta unidadDeVenta;

    // Un empleado tiene un usuario
    @OneToOne(mappedBy = "staff", fetch = FetchType.LAZY)
    private Usuario usuario;
}