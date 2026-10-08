package com.unla.grupo5.services.impl;

import com.unla.grupo5.dtos.EmpleadoRequestDTO;
import com.unla.grupo5.entities.*;
import com.unla.grupo5.repositories.*;
import com.unla.grupo5.services.IEmailService;
import com.unla.grupo5.services.IEmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.unla.grupo5.entities.enums.EnumRoles;

import java.util.UUID;
import java.time.LocalDate;
import java.time.Period;

@Service
public class EmpleadoService implements IEmpleadoService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CocineroRepository cocineroRepository;

    @Autowired
    private CajeroRepository cajeroRepository;

    @Autowired
    private CategoriaCocineroRepository categoriaCocineroRepository;

    @Autowired
    private TurnoCajeroRepository turnoCajeroRepository;

    @Autowired
    private UnidadDeVentaRepository unidadDeVentaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private IEmailService emailService;

    private void validarDatosEmpleado(EmpleadoRequestDTO dto) {
        int edad = Period.between(dto.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < 18) {
            throw new IllegalArgumentException("El empleado debe ser mayor de 18 años.");
        }

        if (dto.getFechaIngreso().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser posterior al día de hoy.");
        }

        if (staffRepository.findByDni(dto.getDni()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un empleado registrado con el DNI " + dto.getDni());
        }
    }

    @Override
    public Staff registrarEmpleado(EmpleadoRequestDTO dto) {
        validarDatosEmpleado(dto);
        UnidadDeVenta unidad = unidadDeVentaRepository.findById(dto.getIdUnidadVenta())
                .orElseThrow(() -> new IllegalArgumentException("La Unidad de Venta seleccionada no existe."));

        Staff empleado;

        if ("COCINERO".equalsIgnoreCase(dto.getTipoEmpleado())) {
            Cocinero cocinero = new Cocinero();
            if (dto.getIdCategoriaCocinero() != null) {
                CategoriaCocinero cat = categoriaCocineroRepository.findById(dto.getIdCategoriaCocinero())
                        .orElseThrow(() -> new IllegalArgumentException("La categoría de cocinero no existe."));
                cocinero.setCategoria(cat);
            }
            empleado = cocinero;

        } else if ("CAJERO".equalsIgnoreCase(dto.getTipoEmpleado())) {
            Cajero cajero = new Cajero();
            if (dto.getIdTurnoCajero() != null) {
                TurnoCajero turno = turnoCajeroRepository.findById(dto.getIdTurnoCajero())
                        .orElseThrow(() -> new IllegalArgumentException("El turno de cajero no existe."));
                cajero.setTurnoTrabajo(turno);
            }
            empleado = cajero;

        } else {
            throw new IllegalArgumentException("Tipo de empleado no válido.");
        }

        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setDni(dto.getDni());
        empleado.setFechaNacimiento(dto.getFechaNacimiento());
        empleado.setFechaIngreso(dto.getFechaIngreso());
        empleado.setSueldo(100000.0);
        empleado.setActivo(true);
        empleado.setUnidadDeVenta(unidad);

        Staff empleadoGuardado = staffRepository.save(empleado);

        String passwordGenerada = UUID.randomUUID().toString().substring(0, 8);

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getEmail());
        usuario.setPassword(passwordGenerada);
        usuario.setActivo(true);
        usuario.setStaff(empleadoGuardado);
        usuarioRepository.save(usuario);

        Rol rol = new Rol();
        rol.setRol(EnumRoles.EMPLEADO);
        rol.setUsuario(usuario);
        rolRepository.save(rol);

        try {
            emailService.enviarCredencialesEmpleado(dto.getEmail(), usuario.getNombreUsuario(), passwordGenerada);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de credenciales: " + e.getMessage());
        }

        return empleadoGuardado;
    }
}