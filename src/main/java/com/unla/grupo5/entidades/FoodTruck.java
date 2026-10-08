package com.unla.grupo5.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "foodtruck")
@PrimaryKeyJoinColumn(name = "idFoodTruck")
@Getter
@Setter
@NoArgsConstructor
public class FoodTruck extends UnidadDeVenta{
    @Column(name = "patente")
    private String patente;

    @Column(name = "estado_conexion")
    private boolean conexion;
}
