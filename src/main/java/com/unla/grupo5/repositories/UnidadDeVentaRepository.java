package com.unla.grupo5.repositories;

import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadDeVentaRepository extends JpaRepository<UnidadDeVenta, Long> {

    @Query("SELECT DISTINCT u FROM UnidadDeVenta u LEFT JOIN FETCH u.lstPedidos WHERE u.festival.id = :idFestival")
    List<UnidadDeVenta> findByFestivalIdWithPedidos(@Param("idFestival") Long idFestival);

    @Query("SELECT DISTINCT u FROM UnidadDeVenta u LEFT JOIN FETCH u.lstPedidos WHERE u.id = :idUnidad")
    Optional<UnidadDeVenta> findByIdWithPedidos(@Param("idUnidad") Long idUnidad);

    List<UnidadDeVenta> findByFestivalId(Long idFestival);

    boolean existsByCodigoAndFestivalId(String codigo, Long idFestival);

    // Igual que la anterior pero ignorando la propia unidad (para la modificación)
    boolean existsByCodigoAndFestivalIdAndIdNot(String codigo, Long idFestival, Long id);
    Optional<UnidadDeVenta> findByResponsable(Usuario responsable);

    //Consulta para obtener el ultimo codigo registrado segun el prefijo
    @Query("SELECT MAX(u.codigo) FROM UnidadDeVenta u WHERE u.codigo LIKE :prefijo%")
    String findMaxCodigoByPrefijo(@Param("prefijo") String prefijo);

    /**
     * Obtiene las unidades de venta de un festival agrupadas y ordenadas de mayor a menor según la cantidad de pedidos.
     * Devuelve una lista de arreglos Object[] donde:
     * - [0] es la entidad UnidadDeVenta.
     * - [1] es la cantidad de pedidos (COUNT(p)).
     * Se utiliza un JOIN explícito para no requerir un mapeo @OneToMany bidireccional en la entidad.
     */
    @Query("SELECT u, COUNT(p) FROM UnidadDeVenta u LEFT JOIN Pedido p ON p.unidadDeVenta = u WHERE u.festival.id = :idFestival GROUP BY u ORDER BY COUNT(p) DESC")
    List<Object[]> findByFestivalIdOrderByCantidadPedidosDesc(@Param("idFestival") Long idFestival);
}
