package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("UsuarioRepository")
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByNombreUsuario(String nombreUsuario);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.nombreUsuario = (:nombreUsuario)")
    public abstract Usuario findByUsuarioAndFetchRol(@Param("nombreUsuario") String nombreUsuario);
}