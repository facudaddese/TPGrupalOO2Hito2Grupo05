package com.unla.grupo5.services.impl;

import com.unla.grupo5.dtos.EmpleadoRequestDTO;
import com.unla.grupo5.entities.*;
import com.unla.grupo5.repositories.*;
import com.unla.grupo5.services.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class EmpleadoService implements com.unla.grupo5.services.EmpleadoService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CocineroRepository cocineroRepository;

    @Autowired
    private CajeroRepository cajeroRepository;

    @Autowired
    private UnidadDeVentaRepository unidadDeVentaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EmailService emailService;

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
            cocinero.setCategoria(dto.getCategoriaCocinero());
            empleado = cocinero;

        } else if ("CAJERO".equalsIgnoreCase(dto.getTipoEmpleado())) {
            Cajero cajero = new Cajero();
            cajero.setTurnoTrabajo(dto.getTurnoCajero());
            empleado = cajero;

        } else {
            throw new IllegalArgumentException("Tipo de empleado no válido.");
        }

        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setDni(dto.getDni());
        empleado.setFechaNacimiento(dto.getFechaNacimiento());
        empleado.setFechaIngreso(dto.getFechaIngreso());
        empleado.setSueldo(dto.getSueldo());
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

        Rol rol = rolRepository.findByRol("ROLE_EMPLEADO")
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setRol("ROLE_EMPLEADO");
                    return rolRepository.save(r);
                });

        usuario.setRol(rol);
        usuarioRepository.save(usuario);

        try {
            emailService.enviarCredencialesEmpleado(dto.getEmail(), usuario.getNombreUsuario(), passwordGenerada);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de credenciales: " + e.getMessage());
        }

        return empleadoGuardado;
    }

    @Override
    public Staff buscarPorId(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El empleado no existe con el ID: " + id));
    }

    @Override
    public List<Staff> traerTodos() {
        return staffRepository.findAll();
    }

    @Override
    @Transactional
    public Staff modificarEmpleado(Long id, EmpleadoRequestDTO dto) {
        Staff empleado = buscarPorId(id);

        if (!Objects.equals(empleado.getDni(), dto.getDni())) {
            if (staffRepository.findByDni(dto.getDni()).isPresent()) {
                throw new IllegalArgumentException("Ya existe otro empleado registrado con el DNI " + dto.getDni());
            }
        }

        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setDni(dto.getDni());
        empleado.setFechaNacimiento(dto.getFechaNacimiento());
        empleado.setFechaIngreso(dto.getFechaIngreso());
        if (dto.getSueldo() != null) empleado.setSueldo(dto.getSueldo());

        if (dto.getIdUnidadVenta() != null) {
            UnidadDeVenta unidad = unidadDeVentaRepository.findById(dto.getIdUnidadVenta())
                    .orElseThrow(() -> new IllegalArgumentException("La Unidad de Venta seleccionada no existe."));
            empleado.setUnidadDeVenta(unidad);
        }

        if (empleado instanceof Cocinero && dto.getCategoriaCocinero() != null) {
            ((Cocinero) empleado).setCategoria(dto.getCategoriaCocinero());
        } else if (empleado instanceof Cajero && dto.getTurnoCajero() != null) {
            ((Cajero) empleado).setTurnoTrabajo(dto.getTurnoCajero());
        }

        return staffRepository.save(empleado);
    }
}