package com.unla.grupo5.services.impl;

import com.unla.grupo5.dtos.PlatoModificarDTO;
import com.unla.grupo5.dtos.PlatoRequestDTO;
import com.unla.grupo5.entities.Plato;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.repositories.PlatoRepository;
import com.unla.grupo5.repositories.UnidadDeVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlatoService implements com.unla.grupo5.services.PlatoService {

    @Autowired
    private PlatoRepository platoRepository;

    @Autowired
    private UnidadDeVentaRepository unidadDeVentaRepository;

    // Alta de un plato asociado a una unidad de venta
    @Override
    @Transactional
    public Plato registrarPlato(PlatoRequestDTO dto) {
        // La unidad de venta elegida tiene que existir
        UnidadDeVenta unidad = unidadDeVentaRepository.findById(dto.getIdUnidadVenta())
                .orElseThrow(() -> new IllegalArgumentException("La unidad de venta seleccionada no existe."));

        // El nombre no puede repetirse dentro de la misma unidad
        if (platoRepository.existsByNombreIgnoreCaseAndUnidadDeVentaId(dto.getNombre().trim(), unidad.getId())) {
            throw new IllegalArgumentException("Ya existe un plato llamado " + dto.getNombre().trim() + " en esta unidad de venta.");
        }

        Plato plato = new Plato();
        plato.setNombre(dto.getNombre().trim());
        plato.setPrecio(dto.getPrecio());
        plato.setCostoProduccion(dto.getCostoProduccion());
        plato.setUnidadDeVenta(unidad);

        return platoRepository.save(plato);
    }

    // Busca un plato por su id; si no existe avisa con un mensaje claro
    @Override
    @Transactional(readOnly = true)
    public Plato buscarPorId(Long id) {
        return platoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El plato no existe con el ID: " + id));
    }

    // Modifica nombre, precio y costo de producción. La unidad de venta no cambia.
    @Override
    @Transactional
    public Plato modificarPlato(Long id, PlatoModificarDTO dto) {
        Plato plato = buscarPorId(id);

        // El nombre no puede coincidir con el de OTRO plato de la misma unidad
        // (se excluye el propio plato para poder guardar sin cambiar el nombre)
        if (platoRepository.existsByNombreIgnoreCaseAndUnidadDeVentaIdAndIdNot(
                dto.getNombre().trim(), plato.getUnidadDeVenta().getId(), id)) {
            throw new IllegalArgumentException("Ya existe otro plato llamado " + dto.getNombre().trim() + " en esta unidad de venta.");
        }

        plato.setNombre(dto.getNombre().trim());
        plato.setPrecio(dto.getPrecio());
        plato.setCostoProduccion(dto.getCostoProduccion());

        return platoRepository.save(plato);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Plato> traerPorUnidad(Long idUnidadVenta) {
        return platoRepository.findByUnidadDeVentaId(idUnidadVenta);
    }
}
