package com.unla.grupo5.entidades;

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
public class Cajero extends Staff{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno_cajero")
    private TurnoCajero turnoTrabajo;

}
