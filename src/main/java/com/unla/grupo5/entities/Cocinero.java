package com.unla.grupo5.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cocinero")
@PrimaryKeyJoinColumn(name = "id_staff")
@Getter
@Setter
@NoArgsConstructor
public class Cocinero extends Staff{
    @Column(name = "especialidad_culinaria")
    private String especialidadCulinaria;

    @Column(name = "plus_por_categoria")
    private double plusPorCategoria;
}
