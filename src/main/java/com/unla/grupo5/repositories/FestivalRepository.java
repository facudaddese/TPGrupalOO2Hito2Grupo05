package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository extends JpaRepository<Festival, Integer> {
}