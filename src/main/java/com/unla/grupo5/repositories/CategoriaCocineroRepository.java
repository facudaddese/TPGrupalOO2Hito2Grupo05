package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.CategoriaCocinero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaCocineroRepository extends JpaRepository<CategoriaCocinero, Long> {
}