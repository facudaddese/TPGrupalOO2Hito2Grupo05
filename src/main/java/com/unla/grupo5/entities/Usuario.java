package com.unla.grupo5.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

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
    private String nombreUsuario; //email

    @Column(name = "password_usuario", nullable = false)
    private String password;

    @Column(name = "usuario_activo", nullable = false)
    private boolean activo = true; //Habilitado o suspendido

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_staff")
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    // Muchos usuarios pertenecen a una sola unidad de venta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_venta")
    private UnidadDeVenta unidadDeVenta;
    public Usuario(String nombreUsuario, String password, boolean activo) {
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.activo = activo;
    }

}
