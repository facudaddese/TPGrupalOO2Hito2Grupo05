package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnidadDeVentaRepository extends JpaRepository<UnidadDeVenta, Long> {

    Optional<UnidadDeVenta> findByResponsable(Usuario responsable);
}