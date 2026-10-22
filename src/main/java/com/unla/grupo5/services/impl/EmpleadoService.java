package com.unla.grupo5.services.impl;

import com.unla.grupo5.dtos.EmpleadoRequestDTO;
import com.unla.grupo5.dtos.LiquidacionHaberesDTO;
import com.unla.grupo5.entities.*;
import com.unla.grupo5.exceptions.RecursoNoEncontradoException;
import com.unla.grupo5.repositories.*;
import com.unla.grupo5.services.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    private FestivalRepository festivalRepository;

    @Autowired
    private EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    public EmpleadoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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
        usuario.setPassword(passwordEncoder.encode(passwordGenerada));
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

    // Empleados de una unidad de venta (los del responsable logueado)
    @Override
    public List<Staff> traerPorUnidad(Long idUnidadVenta) {
        return staffRepository.findByUnidadDeVentaId(idUnidadVenta);
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

    @Override
    @Transactional
    public void darDeBajaEmpleado(Long id) {
        Staff empleado = buscarPorId(id);
        empleado.setActivo(false);
        staffRepository.save(empleado);
    }

    private double calcularFactorDuracion(long dias) {
        double factor;

        if (dias <= 15) {
            factor = 0.5;
        } else if (dias <= 30) {
            factor = 1.0;
        } else {
            factor = ((dias / 15) + 1) * 0.5;
        }
        return factor;
    }

    private double obtenerPlusCocinero(Cocinero cocinero, Costo costo) {
        double plus;
        String categoria = cocinero.getCategoria().toUpperCase();

        if (categoria.equals("COCINERO")) {
            plus = costo.getPlusCocinero();
        } else if (categoria.equals("AYUDANTE")) {
            plus = costo.getPlusAyudante();
        } else {
            plus = costo.getPlusLavaplatos();
        }

        return plus;
    }

    @Override
    public LiquidacionHaberesDTO calcularHaberes(Long idEmpleado, Long idFestival) {
        Staff empleado = staffRepository.findById(idEmpleado).orElseThrow(() -> new RecursoNoEncontradoException("Empleado", idEmpleado));
        Festival festival = festivalRepository.findById(idFestival).orElseThrow(() -> new RecursoNoEncontradoException("Festival", idFestival));

        Costo costo = festival.getCosto();

        long diasFestival = ChronoUnit.DAYS.between(festival.getFechaInicio(), festival.getFechaFin()) + 1;

        double factor = calcularFactorDuracion(diasFestival);

        double sueldoBase = costo.getSueldoBase();
        double montoAdicional = 0;
        double sueldoCalculado = 0;
        String tipoEmpleado = null;
        String conceptoAdicional = null;

        if (empleado instanceof Cocinero cocinero) {
            double plus = obtenerPlusCocinero(cocinero, costo);
            tipoEmpleado = "Cocinero";
            conceptoAdicional = "Plus " + cocinero.getCategoria();
            montoAdicional = plus;
            sueldoCalculado = (sueldoBase + montoAdicional) * factor;

        } else if (empleado instanceof Cajero cajero) {
            int aniosAntiguedad = Period.between(cajero.getFechaIngreso(), festival.getFechaInicio()).getYears();

            montoAdicional = aniosAntiguedad * costo.getAntiguedadCajero();
            tipoEmpleado = "Cajero";
            conceptoAdicional = "Antigüedad: " + aniosAntiguedad + " años";
            sueldoCalculado = (sueldoBase + montoAdicional) * factor;
        }

        return new LiquidacionHaberesDTO(empleado.getId(), empleado.getNombre() + " " + empleado.getApellido(), tipoEmpleado, sueldoBase, conceptoAdicional, montoAdicional, factor, sueldoCalculado);
    }
}