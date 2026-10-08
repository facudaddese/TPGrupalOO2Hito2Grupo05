package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.Cocinero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CocineroRepository extends JpaRepository<Cocinero, Long> {
}