package com.unla.grupo5.services;

import com.unla.grupo5.entities.UnidadDeVenta;
import java.util.List;

public interface UnidadDeVentaService {
    List<UnidadDeVenta> traerPorFestival(Long festivalId);
}
