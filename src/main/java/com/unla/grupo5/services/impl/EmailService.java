package com.unla.grupo5.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements com.unla.grupo5.services.EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void enviarCredencialesEmpleado(String emailDestino, String nombreUsuario, String password) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(emailDestino);
            mensaje.setSubject("Bienvenido a Epicentro Gourmet - Credenciales de Acceso");
            mensaje.setText("Hola!\n\n"
                    + "Se ha registrado tu alta como empleado en el sistema Epicentro Gourmet.\n"
                    + "Tus datos para iniciar sesión son:\n\n"
                    + "Usuario: " + nombreUsuario + "\n"
                    + "Contraseña temporal: " + password + "\n\n"
                    + "Por favor, no compartas esta información con nadie.");

            mailSender.send(mensaje);
            System.out.println("Correo de credenciales enviado con éxito a " + emailDestino);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo a " + emailDestino + ". Motivo: " + e.getMessage());
        }
    }
}