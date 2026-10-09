package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.UnidadDeVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadDeVentaRepository extends JpaRepository<UnidadDeVenta, Long> {
}