package com.unla.grupo5.services.impl;

import com.unla.grupo5.dtos.*;
import com.unla.grupo5.entities.*;
import com.unla.grupo5.repositories.*;
import com.unla.grupo5.services.EmailService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class UnidadDeVentaService implements com.unla.grupo5.services.UnidadDeVentaService {

    // El usuario que se crea en el alta siempre es el responsable de la unidad
    private static final String ROL_RESPONSABLE = "RESPONSABLE";
    // Tipos de unidad que se pueden elegir en el alta
    private static final String TIPO_FOODTRUCK = "FOODTRUCK";
    private static final String TIPO_PUESTO_DESARMABLE = "PUESTO_DESARMABLE";

    @Autowired
    private UnidadDeVentaRepository unidadDeVentaRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    // Envía las credenciales por e-mail (mismo servicio que usa empleados)
    @Autowired
    private EmailService emailService;

    // Codifica la contraseña antes de guardarla (nunca se guarda en texto plano)
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private StaffRepository staffRepository;

    // Devuelve las unidades de venta que pertenecen al festival indicado
    @Override
    @Transactional(readOnly = true)
    public List<UnidadDeVenta> listarPorFestival(Long idFestival) {
        return unidadDeVentaRepository.findByFestivalId(idFestival);
    }

    // Reglas de negocio del alta. Si alguna falla se lanza IllegalArgumentException
    // y el controlador muestra el mensaje en el formulario.
    private void validarDatos(UnidadDeVentaRequestDTO dto) {
        if (usuarioRepository.existsByNombreUsuario(dto.getEmail())) {
            throw new IllegalArgumentException("El e-mail " + dto.getEmail() + " ya está en uso como usuario.");
        }
    }

    // Crea la subclase que corresponde al tipo elegido y valida sus datos propios
    private UnidadDeVenta crearUnidadSegunTipo(UnidadDeVentaRequestDTO dto) {
        if (TIPO_FOODTRUCK.equals(dto.getTipoUnidad())) {
            if (dto.getPatente() == null || dto.getPatente().isBlank()) {
                throw new IllegalArgumentException("La patente es obligatoria para un Food Truck.");
            }
            FoodTruck foodTruck = new FoodTruck();
            foodTruck.setPatente(dto.getPatente().trim().toUpperCase());
            foodTruck.setConexion(dto.isConexion());
            return foodTruck;
        }

        if (TIPO_PUESTO_DESARMABLE.equals(dto.getTipoUnidad())) {
            if (dto.getCantidadCarpas() == null || dto.getCantidadCarpas() < 1) {
                throw new IllegalArgumentException("La cantidad de carpas debe ser mayor a 0.");
            }
            if (dto.getTiempoMontaje() == null || dto.getTiempoMontaje() <= 0) {
                throw new IllegalArgumentException("El tiempo de montaje debe ser mayor a 0.");
            }
            PuestoDesarmable puesto = new PuestoDesarmable();
            puesto.setCantidadCarpas(dto.getCantidadCarpas());
            puesto.setTiempoMontaje(dto.getTiempoMontaje());
            return puesto;
        }

        throw new IllegalArgumentException("El tipo de unidad seleccionado no es válido.");
    }

    // Alta de una unidad de venta junto con su usuario.
    @Override
    @Transactional
    public UnidadDeVenta registrarUnidad(UnidadDeVentaRequestDTO dto) {
        Festival festival = festivalRepository.findById(dto.getIdFestival())
                .orElseThrow(() -> new IllegalArgumentException("El festival seleccionado no existe."));

        validarDatos(dto);

        Rol rol = rolRepository.findByRol(ROL_RESPONSABLE)
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setRol(ROL_RESPONSABLE);
                    return rolRepository.save(r);
                });

        UnidadDeVenta unidad = crearUnidadSegunTipo(dto);
        unidad.setNombreComercial(dto.getNombreComercial());
        unidad.setCodigo(generarCodigoAutomatico(unidad));
        unidad.setSuperficie(dto.getSuperficie());
        unidad.setFestival(festival);
        unidad = unidadDeVentaRepository.save(unidad);

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setActivo(true);
        usuario.setRol(rol);
        usuario.setUnidadDeVenta(unidad);
        usuario = usuarioRepository.save(usuario);

        unidad.setResponsable(usuario);
        unidad = unidadDeVentaRepository.save(unidad);

        try {
            emailService.enviarCredencialesEmpleado(dto.getEmail(), usuario.getNombreUsuario(), dto.getPassword());
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de credenciales: " + e.getMessage());
        }

        return unidad;
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadDeVenta buscarPorId(Long id) {
        return unidadDeVentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La unidad de venta no existe con el ID: " + id));
    }

    @Override
    @Transactional
    public UnidadDeVenta modificarUnidad(Long id, UnidadDeVentaModificarDTO dto) {
        UnidadDeVenta unidad = buscarPorId(id);

        if (unidadDeVentaRepository.existsByCodigoAndFestivalIdAndIdNot(dto.getCodigo(), unidad.getFestival().getId(), id)) {
            throw new IllegalArgumentException("Ya existe otra unidad de venta con el código " + dto.getCodigo() + " en este festival.");
        }

        unidad.setNombreComercial(dto.getNombreComercial());
        unidad.setCodigo(dto.getCodigo());
        unidad.setSuperficie(dto.getSuperficie());

        return unidadDeVentaRepository.save(unidad);
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadDeVenta getUdVByUser(String nombreUsuario) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe: " + nombreUsuario);
        }

        UnidadDeVenta unidad = usuario.getUnidadDeVenta();
        if (unidad == null) {
            throw new IllegalArgumentException("El usuario " + nombreUsuario + " no tiene una unidad de venta asignada.");
        }
        unidad.getNombreComercial();
        return unidad;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadDeVenta> listarPorFestivalOrdenadoPorRecaudacion(Long idFestival, String orden) {
        List<UnidadDeVenta> unidades = unidadDeVentaRepository.findByFestivalIdWithPedidos(idFestival);

        if ("asc".equalsIgnoreCase(orden)) {
            unidades.sort(Comparator.comparingDouble(UnidadDeVenta::getRecaudacionTotal));
        } else {
            unidades.sort(Comparator.comparingDouble(UnidadDeVenta::getRecaudacionTotal).reversed());
        }

        return unidades;
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteVentaDTO generarReporteRecaudacion(Long idFestival, String nombreUsuario) {
        Festival festival = festivalRepository.findById(idFestival)
                .orElseThrow(() -> new IllegalArgumentException("El festival seleccionado no existe."));

        UnidadDeVenta unidad = getUdVByUser(nombreUsuario);

        double recaudacion = 0.0;
        int cantidadPedidos = 0;

        boolean participo = unidad.getFestival() != null && unidad.getFestival().getId().equals(idFestival);

        if (participo) {
            UnidadDeVenta unidadConPedidos = unidadDeVentaRepository.findByIdWithPedidos(unidad.getId())
                    .orElse(unidad);
            recaudacion = unidadConPedidos.getRecaudacionTotal();
            cantidadPedidos = unidadConPedidos.getLstPedidos() != null ? unidadConPedidos.getLstPedidos().size() : 0;
        }

        return new ReporteVentaDTO(
                festival.getId(),
                festival.getNombre(),
                unidad.getId(),
                unidad.getNombreComercial(),
                unidad.getCodigo(),
                recaudacion,
                cantidadPedidos,
                participo
        );
    }

    private CanonDTO armarDetalleCanon(UnidadDeVenta unidad, Festival festival) {
        unidad = (UnidadDeVenta) Hibernate.unproxy(unidad);
        Costo costo = festival.getCosto();
        if (costo == null) {
            throw new IllegalArgumentException("El festival " + festival.getNombre() + " no tiene los costos configurados.");
        }

        CanonDTO detalle = new CanonDTO();
        detalle.setNombreFestival(festival.getNombre());
        detalle.setNombreComercial(unidad.getNombreComercial());
        detalle.setCodigo(unidad.getCodigo());
        detalle.setTipoUnidad(obtenerTipoUnidad(unidad));

        int superficie = unidad.getSuperficie() == null ? 0 : unidad.getSuperficie();
        detalle.setSuperficie(superficie);
        detalle.setCostoSuperficie(costo.getCostoSuperficies());
        detalle.setMontoSuperficie(superficie * costo.getCostoSuperficies());

        double canon = detalle.getMontoSuperficie();

        if (unidad instanceof FoodTruck foodTruck) {
            detalle.setEsFoodTruck(true);
            detalle.setRequiereConexion(foodTruck.isConexion());
            if (foodTruck.isConexion()) {
                detalle.setPlusElectricidad(costo.getPlusElectricidad());
                canon += costo.getPlusElectricidad();
            }
        } else if (unidad instanceof PuestoDesarmable puesto) {
            detalle.setEsPuestoDesarmable(true);
            detalle.setTiempoMontaje(puesto.getTiempoMontaje());
            detalle.setCostoMontaje(costo.getCostoMontaje());
            detalle.setDescuentoMontaje(puesto.getTiempoMontaje() * costo.getCostoMontaje());
            canon -= detalle.getDescuentoMontaje();
        }

        detalle.setCanon(canon);
        return detalle;
    }

    private double calcularCanon(UnidadDeVenta unidad, Festival festival) {
        return armarDetalleCanon(unidad, festival).getCanon();
    }

    @Override
    @Transactional(readOnly = true)
    public CanonDTO calcularCanonUnidad(String nombreUsuario) {
        UnidadDeVenta unidad = getUdVByUser(nombreUsuario);
        if (unidad.getFestival() == null) {
            throw new IllegalArgumentException("Tu unidad de venta no tiene un festival asignado.");
        }
        return armarDetalleCanon(unidad, unidad.getFestival());
    }

    private String obtenerTipoUnidad(UnidadDeVenta unidad) {
        unidad = (UnidadDeVenta) Hibernate.unproxy(unidad);
        if (unidad instanceof FoodTruck) {
            return "Food Truck";
        }
        if (unidad instanceof PuestoDesarmable) {
            return "Puesto Desarmable";
        }
        return "Sin tipo asignado";
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteMayoresCanonDTO> obtenerUnidadesConMayorCanon(Long idFestival) {
        Festival festival = festivalRepository.findById(idFestival)
                .orElseThrow(() -> new IllegalArgumentException("El festival seleccionado no existe."));

        List<ReporteMayoresCanonDTO> reporte = new ArrayList<>();
        for (UnidadDeVenta unidad : unidadDeVentaRepository.findByFestivalId(idFestival)) {
            reporte.add(new ReporteMayoresCanonDTO(
                    unidad.getNombreComercial(),
                    unidad.getCodigo(),
                    obtenerTipoUnidad(unidad),
                    calcularCanon(unidad, festival)
            ));
        }

        reporte.sort(Comparator.comparingDouble(ReporteMayoresCanonDTO::getCanon).reversed()
                .thenComparing(ReporteMayoresCanonDTO::getNombreComercial));

        return reporte;
    }

    public double calcularTotalSueldos(UnidadDeVenta unidad, Festival festival) {
        double totalSueldos = 0.0;
        List<Staff> empleados = staffRepository.findByUnidadDeVentaId(unidad.getId());
        LocalDate fechaCorte = LocalDate.now();

        for (Staff empleado : empleados) {
            List<LiquidacionHaberesDTO> liquidaciones = empleadoService.calcularHaberesHastaFecha(empleado.getId(), festival.getId(), fechaCorte);
            for (LiquidacionHaberesDTO liquidacion : liquidaciones) {
                totalSueldos += liquidacion.getSueldoCalculado();
            }
        }
        return totalSueldos;
    }

    public RentabilidadNetaDTO calcularRentabilidadNeta(UnidadDeVenta unidad, Festival festival) {
        double totalVentas = unidad.getRecaudacionTotal();
        double costoPlatos = unidad.getCostoTotal();
        double totalSueldos = calcularTotalSueldos(unidad, festival);
        double canon = calcularCanon(unidad, festival);

        double rentabilidadNeta = (totalVentas - costoPlatos) - totalSueldos - canon;

        return new RentabilidadNetaDTO(totalVentas, costoPlatos, totalSueldos, canon, rentabilidadNeta);
    }

    public double calcularTotalSueldosEntreFechas(UnidadDeVenta unidad, Festival festival, LocalDate fechaDesde, LocalDate fechaHasta) {
        double totalSueldos = 0.0;
        List<Staff> empleados = staffRepository.findByUnidadDeVentaId(unidad.getId());

        for (Staff empleado : empleados) {
            List<LiquidacionHaberesDTO> liquidaciones = empleadoService.calcularHaberesHastaFecha(empleado.getId(), festival.getId(), fechaHasta);

            for (LiquidacionHaberesDTO liquidacion : liquidaciones) {
                LocalDate inicioPeriodo = liquidacion.getPeriodoInicio();
                LocalDate finPeriodo = liquidacion.getPeriodoFin();

                if (!(finPeriodo.isBefore(fechaDesde) || inicioPeriodo.isAfter(fechaHasta))) {
                    if (!inicioPeriodo.isBefore(fechaDesde) && !finPeriodo.isAfter(fechaHasta)) {
                        totalSueldos += liquidacion.getSueldoCalculado();
                    } else {
                        LocalDate inicioEfectivo = inicioPeriodo.isBefore(fechaDesde) ? fechaDesde : inicioPeriodo;
                        LocalDate finEfectivo = finPeriodo.isAfter(fechaHasta) ? fechaHasta : finPeriodo;

                        long diasEnRango = ChronoUnit.DAYS.between(inicioEfectivo, finEfectivo) + 1;
                        long diasTotalesMes = liquidacion.getDiasLiquidacion();

                        if (diasTotalesMes > 0) {
                            double proporcion = (double) diasEnRango / diasTotalesMes;
                            totalSueldos += liquidacion.getSueldoCalculado() * proporcion;
                        } else {
                            totalSueldos += liquidacion.getSueldoCalculado();
                        }
                    }
                }
            }
        }
        return totalSueldos;
    }

    private double calcularCanonEntreFechas(UnidadDeVenta unidad, Festival festival, LocalDate fechaDesde, LocalDate fechaHasta) {
        double canonTotal = calcularCanon(unidad, festival);
        LocalDate inicioFestival = festival.getFechaInicio();
        LocalDate finFestival = festival.getFechaFin();

        long diasFestival = ChronoUnit.DAYS.between(inicioFestival, finFestival) + 1;

        if (diasFestival <= 0) {
            return 0.0;
        }

        long diasConsultados = ChronoUnit.DAYS.between(fechaDesde, fechaHasta) + 1;

        return canonTotal * diasConsultados / diasFestival;
    }

    public RentabilidadNetaDTO calcularRentabilidadNetaEntreFechas(UnidadDeVenta unidad, Festival festival, LocalDate fechaDesde, LocalDate fechaHasta) {
        double totalVentas = unidad.getRecaudacionEntreFechas(fechaDesde, fechaHasta);
        double costoPlatos = unidad.getCostoTotalEntreFechas(fechaDesde, fechaHasta);
        double totalSueldos = calcularTotalSueldosEntreFechas(unidad, festival, fechaDesde, fechaHasta);
        double canon = calcularCanonEntreFechas(unidad, festival, fechaDesde, fechaHasta);

        double rentabilidadNeta = (totalVentas - costoPlatos) - totalSueldos - canon;

        return new RentabilidadNetaDTO(totalVentas, costoPlatos, totalSueldos, canon, rentabilidadNeta);
    }

    private String generarCodigoAutomatico(UnidadDeVenta unidad) {
        String prefijo = (unidad instanceof FoodTruck) ? "FT-" : "PD-";
        String maxCodigo = unidadDeVentaRepository.findMaxCodigoByPrefijo(prefijo);

        int siguienteNumero = 1;
        if (maxCodigo != null && maxCodigo.length() >= 4) {
            String numeroStr = maxCodigo.substring(3);
            try {
                siguienteNumero = Integer.parseInt(numeroStr) + 1;
            } catch (NumberFormatException e) {
                siguienteNumero = 1;
            }
        }
        return String.format("%s%03d", prefijo, siguienteNumero);
    }

    @Override
    public List<UnidadDeVenta> listarPorFestivalOrdenadoPorCantidadPedidos(Long idFestival) {
        List<Object[]> resultados = unidadDeVentaRepository.findByFestivalIdOrderByCantidadPedidosDesc(idFestival);
        List<UnidadDeVenta> unidades = new ArrayList<>();

        for (Object[] fila : resultados) {
            UnidadDeVenta unidad = (UnidadDeVenta) fila[0];
            Long cantidad = (Long) fila[1];
            unidad.setCantidadPedidos(cantidad);
            unidades.add(unidad);
        }

        return unidades;
    }

    @Override
    @Transactional
    public void cerrarCaja(Long idUnidad) {
        UnidadDeVenta unidad = buscarPorId(idUnidad);
        if (!unidad.isCajaAbierta()) {
            throw new IllegalArgumentException("La caja ya se encuentra cerrada.");
        }
        unidad.setCajaAbierta(false);
        unidadDeVentaRepository.save(unidad);
    }

    @Override
    @Transactional
    public void abrirCaja(Long idUnidad) {
        UnidadDeVenta unidad = buscarPorId(idUnidad);
        if (unidad.isCajaAbierta()) {
            throw new IllegalArgumentException("La caja ya se encuentra abierta.");
        }
        unidad.setCajaAbierta(true);
        unidadDeVentaRepository.save(unidad);
    }
}