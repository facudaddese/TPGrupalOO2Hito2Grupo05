package com.unla.grupo5.entities;

import com.unla.grupo5.entities.enums.EnumTurnos;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Turno_Cajero")
@Getter
@Setter
@NoArgsConstructor
public class TurnoCajero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "Turno")
    @Enumerated(EnumType.STRING)
    private EnumTurnos turno;
}
