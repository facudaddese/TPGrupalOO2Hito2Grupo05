package com.unla.grupo5.services.impl;

import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.repositories.UnidadDeVentaRepository;
import com.unla.grupo5.services.UnidadDeVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("unidadDeVentaService")
public class UnidadDeVentaServiceImpl implements UnidadDeVentaService {

    @Autowired
    private UnidadDeVentaRepository unidadDeVentaRepository;

    @Override
    public List<UnidadDeVenta> traerPorFestival(Long festivalId){
        return unidadDeVentaRepository.findByFestivalId(festivalId);
    }

}
