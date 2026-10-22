package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.Staff;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByDni(Long dni);

    List<Staff> findByActivoTrue();

    // Todos los empleados de una unidad de venta (activos y dados de baja)
    List<Staff> findByUnidadDeVentaId(Long idUnidadVenta);

    List<Staff> findByUnidadDeVentaIdAndActivoTrue(Long idUnidadVenta);

    List<Staff> findByUnidadDeVentaAndActivoTrue(UnidadDeVenta unidadDeVenta);

    @Query("SELECT s FROM Staff s JOIN FETCH s.usuario WHERE s.usuario.nombreUsuario = :nombreUsuario")
    Optional<Staff> findByUsuarioNombreUsuario(@Param("nombreUsuario") String nombreUsuario);
}