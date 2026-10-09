package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "puestoDesarmable")
@PrimaryKeyJoinColumn(name = "idPuestoDesarmable")
@Getter
@Setter
@NoArgsConstructor
public class PuestoDesarmable extends UnidadDeVenta{

    @Column(name = "cantidad_carpas")
    private Integer cantidadCarpas;

    @Column(name = "tiempo_montaje")
    private double tiempoMontaje;

}
