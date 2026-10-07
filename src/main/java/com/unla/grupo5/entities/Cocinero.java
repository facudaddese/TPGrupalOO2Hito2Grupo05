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
public class Cocinero extends Staff{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_cocinero")
    private CategoriaCocinero categoria;

}
