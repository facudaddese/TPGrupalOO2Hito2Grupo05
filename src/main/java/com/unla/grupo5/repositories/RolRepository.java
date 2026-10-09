package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("rolRepository")
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByRol(String rol);
}