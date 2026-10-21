package com.unla.grupo5.exceptions;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " con id " + id + " no encontrado.");
    }
    public RecursoNoEncontradoException(String recurso) {
        super(recurso +  " no encontrado.");
    }
}
