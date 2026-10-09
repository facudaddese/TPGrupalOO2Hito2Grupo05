package com.unla.grupo5.services;

public interface EmailService {
    // Contrato del mail
    void enviarCredencialesEmpleado(String emailDestino, String nombreUsuario, String password);
}