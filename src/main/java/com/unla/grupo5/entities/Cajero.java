package com.unla.grupo5.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
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
    @Column(nullable = false)
    private String turno;
}
