package com.unla.grupo5.exceptions;

public class UnidadDeVentaNoAsignadaException extends RuntimeException {

    public UnidadDeVentaNoAsignadaException() {
        super("No posee una Unidad de Venta asignada.");
    }
}
