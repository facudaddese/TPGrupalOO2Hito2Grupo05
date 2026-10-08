package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.TurnoCajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnoCajeroRepository extends JpaRepository<TurnoCajero, Long> {
}