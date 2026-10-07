package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre_usuario", unique = true, nullable = false)
    private String nombreUsuario;

    @Column(name = "password_usuario", nullable = false)
    private String password;

    @Column(name = "usuario_activo", nullable = false)
    private boolean activo = true; //Habilitado o suspendido

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_staff")
    private Staff staff;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario")
    private Set<Rol> lstRoles = new HashSet<>();
}
