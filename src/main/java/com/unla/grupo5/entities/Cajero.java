package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cajero")
@PrimaryKeyJoinColumn(name = "id_staff")
@Getter
@Setter
@NoArgsConstructor
public class Cajero extends Staff {

    @Column(name = "turno_trabajo", length = 100)
    private String turnoTrabajo;
}