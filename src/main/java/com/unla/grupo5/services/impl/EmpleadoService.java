package com.unla.grupo5.services.impl;

import com.unla.grupo5.dtos.EmpleadoRequestDTO;
import com.unla.grupo5.dtos.HistorialTrabajoDTO;
import com.unla.grupo5.dtos.LiquidacionHaberesDTO;
import com.unla.grupo5.entities.*;
import com.unla.grupo5.exceptions.RecursoNoEncontradoException;
import com.unla.grupo5.repositories.*;
import com.unla.grupo5.services.EmailService;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

        if (dto.getFechaIngreso().isBefore(dto.getFechaNacimiento())) {
            throw new IllegalArgumentException(
                    "La fecha de ingreso no puede ser anterior a la fecha de nacimiento."
            );
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Staff registrarEmpleado(EmpleadoRequestDTO dto, String usuarioResponsable) {
        validarDatosEmpleado(dto);

        Usuario responsable = usuarioRepository.findByNombreUsuario(usuarioResponsable);

        // Verificar que sea un responsable.
        if (responsable.getRol() == null ||
                !"RESPONSABLE".equals(responsable.getRol().getRol())) {
            throw new IllegalArgumentException(
                    "El usuario no tiene permisos para registrar empleados."
            );
        }

        UnidadDeVenta unidad = unidadDeVentaRepository.findByResponsable(responsable)
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
        empleado.setUnidadDeVenta(unidad);
        empleado.setActivo(true);
        empleado.setUnidadDeVenta(unidad);
        // Queda registrada en el historial de unidades en las que trabajó
        empleado.getHistorialUnidades().add(unidad);

        Staff empleadoGuardado = staffRepository.save(empleado);

        String passwordGenerada = UUID.randomUUID().toString().substring(0, 8);

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(passwordGenerada));
        usuario.setActivo(true);
        usuario.setStaff(empleadoGuardado);

        usuarioRepository.save(usuario);

        Rol rol = rolRepository.findByRol("EMPLEADO")
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setRol("EMPLEADO");
                    return rolRepository.save(r);
                });

        usuario.setRol(rol);
        usuarioRepository.save(usuario);


        emailService.enviarCredencialesEmpleado(dto.getEmail(), usuario.getNombreUsuario(), passwordGenerada);

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
    public Staff modificarEmpleado(Long id, EmpleadoRequestDTO dto, String usuarioResponsable) {

        Usuario responsable = usuarioRepository.findByNombreUsuario(usuarioResponsable);

        // Verificar que sea un responsable.
        if (responsable.getRol() == null ||
                !"RESPONSABLE".equals(responsable.getRol().getRol())) {
            throw new IllegalArgumentException(
                    "El usuario no tiene permisos para registrar empleados."
            );
        }
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

        UnidadDeVenta unidad = unidadDeVentaRepository.findByResponsable(responsable)
                    .orElseThrow(() -> new IllegalArgumentException("La Unidad de Venta seleccionada no existe."));

            empleado.setUnidadDeVenta(unidad);
            // Si cambia de unidad, la nueva se suma al historial (las anteriores quedan registradas)
            empleado.getHistorialUnidades().add(unidad);
        }

        if (empleado instanceof Cocinero && dto.getCategoriaCocinero() != null) {
            ((Cocinero) empleado).setCategoria(dto.getCategoriaCocinero());
        } else if (empleado instanceof Cajero && dto.getTurnoCajero() != null) {
            ((Cajero) empleado).setTurnoTrabajo(dto.getTurnoCajero());
        }

        empleado.setUnidadDeVenta(unidad);

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
        if (dias <= 0) {
            factor = 0;
        }
        factor = Math.ceil(dias / 15.0) * 0.5;
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

    public List<LiquidacionHaberesDTO> calcularHaberesMensuales(Long idEmpleado, Long idFestival) {

        Staff empleado = staffRepository.findById(idEmpleado).orElseThrow(() ->
                        new RecursoNoEncontradoException("Especializacion del empleado", idEmpleado));

        Festival festival = festivalRepository.findById(idFestival).orElseThrow(() ->
                        new RecursoNoEncontradoException("Festival", idFestival));

        return calcularHaberes(idEmpleado, idFestival, null);
    }

    public List<LiquidacionHaberesDTO> filtrarMesesCerrados(List<LiquidacionHaberesDTO> liquidaciones) {

        YearMonth mesActual = YearMonth.now();

        return liquidaciones.stream()
                .filter(l -> YearMonth.from(l.getPeriodoInicio())
                        .isBefore(mesActual))
                .toList();
    }

    public List<LiquidacionHaberesDTO> calcularHaberesHastaFecha(Long idEmpleado, Long idFestival, LocalDate fechaCorte) {
        return calcularHaberes(idEmpleado, idFestival, fechaCorte);
    }

    private List<LiquidacionHaberesDTO> calcularHaberes(Long idEmpleado, Long idFestival, LocalDate fechaCorte) {

        Staff empleado = staffRepository.findById(idEmpleado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Especializacion del empleado", idEmpleado));

        Festival festival = festivalRepository.findById(idFestival)
                .orElseThrow(() -> new RecursoNoEncontradoException("Festival", idFestival));

        Costo costo = festival.getCosto();

        LocalDate fechaInicio = festival.getFechaInicio();
        LocalDate fechaFin = festival.getFechaFin();

        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("Las fechas del festival no son válidas");
        }

        // Duración total del festival y factor global
        long diasDurFest = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        double factorGlobal = calcularFactorDuracion(diasDurFest);

        // Determinar hasta qué fecha se calculan los haberes.
        LocalDate fechaFinCalculo = fechaFin;

        if (fechaCorte != null && fechaCorte.isBefore(fechaFinCalculo)) {
            fechaFinCalculo = fechaCorte;
        }

        double sueldoBase = costo.getSueldoBase();

        List<LiquidacionHaberesDTO> liquidaciones = new ArrayList<>();

        YearMonth mesActual = YearMonth.from(fechaInicio);
        YearMonth ultimoMes = YearMonth.from(fechaFinCalculo);

        // Si el festival todavía no empezó, el ciclo no se ejecuta.
        while (!fechaFinCalculo.isBefore(fechaInicio) && !mesActual.isAfter(ultimoMes)) {

            LocalDate inicioPeriodo = mesActual.atDay(1);

            if (inicioPeriodo.isBefore(fechaInicio)) {
                inicioPeriodo = fechaInicio;
            }

            LocalDate finPeriodo = mesActual.atEndOfMonth();

            if (finPeriodo.isAfter(fechaFinCalculo)) {
                finPeriodo = fechaFinCalculo;
            }

            // Días trabajados en este mes específico
            long diasMes = ChronoUnit.DAYS.between(inicioPeriodo, finPeriodo) + 1;

            double montoAdicional;
            String tipoEmpleado;
            String conceptoAdicional;

            if (empleado instanceof Cocinero cocinero) {

                montoAdicional = obtenerPlusCocinero(cocinero, costo);
                tipoEmpleado = "Cocinero";
                conceptoAdicional = "Plus " + cocinero.getCategoria();

            } else if (empleado instanceof Cajero cajero) {

                int aniosAntiguedad = Period.between(cajero.getFechaIngreso(), finPeriodo).getYears();
                montoAdicional = aniosAntiguedad * costo.getAntiguedadCajero();

                tipoEmpleado = "Cajero";
                conceptoAdicional = "Antigüedad: " + aniosAntiguedad + " años";

            } else {
                throw new IllegalArgumentException("Tipo de empleado no contemplado: " + empleado.getClass().getSimpleName());
            }

            // Calculo del sueldo total del evento y su respectivo prorrateo mensual
            double sueldoTotalEvento = (sueldoBase + montoAdicional) * factorGlobal;
            double sueldoCalculado = sueldoTotalEvento * ((double) diasMes / diasDurFest);

            liquidaciones.add(new LiquidacionHaberesDTO(empleado.getId(), empleado.getNombre() + " " + empleado.getApellido(),
                    tipoEmpleado, sueldoBase, conceptoAdicional, montoAdicional, factorGlobal, sueldoCalculado, inicioPeriodo, finPeriodo, diasMes));

            mesActual = mesActual.plusMonths(1);
        }

        return liquidaciones;
    }

    @Override
    public void reactivarEmpleado(Long id) {
        Staff empleado = staffRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empleado no encontrado con ID: " + id));

        empleado.setActivo(true);
        staffRepository.save(empleado);
    }

    @Override
    public List<Staff> traerActivosPorUnidad(Long idUnidadVenta) {
        return staffRepository.findByUnidadDeVentaIdAndActivoTrue(idUnidadVenta);
    }

    // Historial de trabajo: festivales y unidades de venta en las que trabajó el empleado,
    // del festival más reciente al más antiguo
    @Override
    @Transactional
    public List<HistorialTrabajoDTO> obtenerHistorialTrabajo(String nombreUsuario) {
        Staff empleado = staffRepository.findByUsuarioNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Puesto de staff"));

        // Se parte del historial y se asegura que la unidad actual esté incluida
        Set<UnidadDeVenta> unidades = new HashSet<>(empleado.getHistorialUnidades());
        if (empleado.getUnidadDeVenta() != null) {
            unidades.add(empleado.getUnidadDeVenta());
        }

        Long idUnidadActual = empleado.getUnidadDeVenta() == null ? null : empleado.getUnidadDeVenta().getId();

        List<HistorialTrabajoDTO> historial = new ArrayList<>();
        for (UnidadDeVenta unidad : unidades) {
            Festival festival = unidad.getFestival();
            historial.add(new HistorialTrabajoDTO(
                    festival.getNombre(),
                    festival.getTemporada(),
                    festival.getFechaInicio(),
                    festival.getFechaFin(),
                    unidad.getNombreComercial(),
                    unidad.getCodigo(),
                    obtenerTipoUnidad(unidad),
                    unidad.getId().equals(idUnidadActual)
            ));
        }

        historial.sort(Comparator.comparing(HistorialTrabajoDTO::getFechaInicio).reversed()
                .thenComparing(HistorialTrabajoDTO::getNombreUnidad));
        return historial;
    }

    // Tipo de la unidad según su subclase (la relación puede venir como proxy de Hibernate)
    private String obtenerTipoUnidad(UnidadDeVenta unidad) {
        Object real = Hibernate.unproxy(unidad);
        if (real instanceof FoodTruck) {
            return "Food Truck";
        }
        if (real instanceof PuestoDesarmable) {
            return "Puesto Desarmable";
        }
        return "Sin tipo asignado";
    }

}