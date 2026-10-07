package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Categoria_Cocinero")
@Getter
@Setter
@NoArgsConstructor
public class CategoriaCocinero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre_categoria")
    private String nombre; //Ej: "Jefe de cocina", "Ayudante", "Parrillero", etc.

    @Column(name = "plus_categoria")
    private long plus;
}
