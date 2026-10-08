package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByDni(int dni);

    List<Staff> findByActivoTrue();

    List<Staff> findByUnidadDeVentaIdAndActivoTrue(int idUnidadVenta);
}