package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cocinero")
@PrimaryKeyJoinColumn(name = "id_staff")
@Getter
@Setter
@NoArgsConstructor
public class Cocinero extends Staff {

    @Column(name = "categoria", length = 100)
    private String categoria;
}