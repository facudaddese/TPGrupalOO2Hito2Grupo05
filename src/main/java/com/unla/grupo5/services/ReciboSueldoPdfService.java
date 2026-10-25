package com.unla.grupo5.services;

import com.unla.grupo5.dtos.LiquidacionHaberesDTO;
import com.unla.grupo5.entities.Festival;
import com.unla.grupo5.entities.UnidadDeVenta;

public interface ReciboSueldoPdfService {

    public byte[] generarRecibo(LiquidacionHaberesDTO liquidacion, Festival festival, UnidadDeVenta unidad);

}
