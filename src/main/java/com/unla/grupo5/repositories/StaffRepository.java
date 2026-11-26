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

    @Query("SELECT s FROM Staff s WHERE " +
            "(:nombre IS NULL OR LOWER(s.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:apellido IS NULL OR LOWER(s.apellido) LIKE LOWER(CONCAT('%', :apellido, '%'))) AND " +
            "(:idUnidadDeVenta IS NULL OR s.unidadDeVenta.id = :idUnidadDeVenta) AND " +
            "(:activo IS NULL OR s.activo = :activo) AND " +
            "(:tipoClase IS NULL OR TYPE(s) = :tipoClase)")
    List<Staff> buscarConFiltros(
            @Param("nombre") String nombre,
            @Param("apellido") String apellido,
            @Param("idUnidadDeVenta") Long idUnidadDeVenta,
            @Param("activo") Boolean activo,
            @Param("tipoClase") Class<? extends Staff> tipoClase
    );
}