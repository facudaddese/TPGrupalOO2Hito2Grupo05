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
    private StaffRepository  staffRepository;

    // Devuelve las unidades de venta que pertenecen al festival indicado
    @Override
    @Transactional(readOnly = true)
    public List<UnidadDeVenta> listarPorFestival(Long idFestival) {
        return unidadDeVentaRepository.findByFestivalId(idFestival);
    }

    // Reglas de negocio del alta. Si alguna falla se lanza IllegalArgumentException
    // y el controlador muestra el mensaje en el formulario.
    private void validarDatos(UnidadDeVentaRequestDTO dto) {
        // El código identifica a la unidad: no puede repetirse dentro del mismo festival
        if (unidadDeVentaRepository.existsByCodigoAndFestivalId(dto.getCodigo(), dto.getIdFestival())) {
            throw new IllegalArgumentException("Ya existe una unidad de venta con el código " + dto.getCodigo() + " en este festival.");
        }

        // El nombre de usuario es único en todo el sistema
        // El e-mail es el nombre de usuario, y es único en todo el sistema
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
    // Todo o nada: si algo falla no queda ni el usuario ni la unidad.
    @Override
    @Transactional
    public UnidadDeVenta registrarUnidad(UnidadDeVentaRequestDTO dto) {
        // 1. El festival elegido tiene que existir
        Festival festival = festivalRepository.findById(dto.getIdFestival())
                .orElseThrow(() -> new IllegalArgumentException("El festival seleccionado no existe."));

        // 2. Validaciones de negocio (código y usuario)
        validarDatos(dto);

        // 3. Busca el rol RESPONSABLE en la tabla; si todavía no existe lo crea
        Rol rol = rolRepository.findByRol(ROL_RESPONSABLE)
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setRol(ROL_RESPONSABLE);
                    return rolRepository.save(r);
                });

        // 4. Se crea la unidad según su tipo y se guarda primero, así el usuario puede quedar asociado a ella
        UnidadDeVenta unidad = crearUnidadSegunTipo(dto);
        unidad.setNombreComercial(dto.getNombreComercial());
        unidad.setCodigo(dto.getCodigo());
        unidad.setSuperficie(dto.getSuperficie());
        unidad.setFestival(festival);
        unidad = unidadDeVentaRepository.save(unidad);

        // 5. Se crea el usuario y se lo asocia a la unidad (siempre pertenece a ella).
        //    El e-mail es el nombre de usuario y la contraseña es la que se completó en el
        //    formulario. Se guarda codificada; en claro solo viaja por e-mail.
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setActivo(true);
        usuario.setRol(rol);
        usuario.setUnidadDeVenta(unidad);
        usuario = usuarioRepository.save(usuario);

        // 6. Ese usuario pasa a ser el responsable de la unidad
        unidad.setResponsable(usuario);
        unidad = unidadDeVentaRepository.save(unidad);

        // 7. Se envían las credenciales por e-mail (si el envío falla no se cancela el alta)
        try {
            emailService.enviarCredencialesEmpleado(dto.getEmail(), usuario.getNombreUsuario(), dto.getPassword());
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de credenciales: " + e.getMessage());
        }

        return unidad;
    }

    // Busca una unidad por su id; si no existe avisa con un mensaje claro
    @Override
    @Transactional(readOnly = true)
    public UnidadDeVenta buscarPorId(Long id) {
        return unidadDeVentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La unidad de venta no existe con el ID: " + id));
    }

    // Modifica los datos de una unidad ya registrada (nombre, código y superficie).
    // El festival, el responsable y los usuarios no cambian.
    @Override
    @Transactional
    public UnidadDeVenta modificarUnidad(Long id, UnidadDeVentaModificarDTO dto) {
        UnidadDeVenta unidad = buscarPorId(id);

        // El código no puede coincidir con el de OTRA unidad del mismo festival
        // (se excluye la propia unidad para poder guardar sin cambiar el código)
        if (unidadDeVentaRepository.existsByCodigoAndFestivalIdAndIdNot(dto.getCodigo(), unidad.getFestival().getId(), id)) {
            throw new IllegalArgumentException("Ya existe otra unidad de venta con el código " + dto.getCodigo() + " en este festival.");
        }

        unidad.setNombreComercial(dto.getNombreComercial());
        unidad.setCodigo(dto.getCodigo());
        unidad.setSuperficie(dto.getSuperficie());

        return unidadDeVentaRepository.save(unidad);
    }

    // Devuelve la unidad de venta del usuario; si no tiene ninguna asignada avisa con un mensaje claro
    @Override
    @Transactional(readOnly = true)
    public UnidadDeVenta getUdVByUser(String nombreUsuario) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuario == null){
            throw new IllegalArgumentException("El usuario no existe: " + nombreUsuario);
        }

        UnidadDeVenta unidad = usuario.getUnidadDeVenta();
        if (unidad == null) {
            throw new IllegalArgumentException("El usuario " + nombreUsuario + " no tiene una unidad de venta asignada.");
        }
        // Se fuerza la carga dentro de la transacción (la relación es LAZY)
        unidad.getNombreComercial();
        return unidad;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadDeVenta> listarPorFestivalOrdenadoPorRecaudacion(Long idFestival, String orden){
        List<UnidadDeVenta> unidades = unidadDeVentaRepository.findByFestivalIdWithPedidos(idFestival);

        if("asc".equalsIgnoreCase(orden)){
            unidades.sort(Comparator.comparingDouble(UnidadDeVenta::getRecaudacionTotal));
        }else{
            unidades.sort(Comparator.comparingDouble(UnidadDeVenta::getRecaudacionTotal).reversed());
        }

        return unidades;

    }

    @Override
    @Transactional(readOnly = true)
    public ReporteVentaDTO generarReporteRecaudacion(Long idFestival, String nombreUsuario){
        //Verificamos la existencia del festival
        Festival festival = festivalRepository.findById(idFestival)
                .orElseThrow(() -> new IllegalArgumentException("El festival seleccionado no existe."));

        //Obtenemos la UDV asociada al usuario
        UnidadDeVenta unidad = getUdVByUser(nombreUsuario);

        double recaudacion = 0.0;
        int cantidadPedidos = 0;

        boolean participo = unidad.getFestival() != null && unidad.getFestival().getId().equals(idFestival);

        //Verificamos que ld UDV pertenezca al festival y si es asi obtenemos su recaudacion y pedidos
        if (participo) {
            UnidadDeVenta unidadConPedidos = unidadDeVentaRepository.findByIdWithPedidos(unidad.getId())
                    .orElse(unidad);
            recaudacion = unidadConPedidos.getRecaudacionTotal();
            cantidadPedidos = unidadConPedidos.getLstPedidos() != null ? unidadConPedidos.getLstPedidos().size() : 0;
        }

        //Retornamos el DTO
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

    // Arma el detalle del canon de una unidad con los costos que fija el festival:
    //  - Food Truck: (superficie * costo por m²) + plus de electricidad si requiere conexión
    //  - Puesto Desarmable: (superficie * costo por m²) - (tiempo de montaje * costo de montaje)
    private CanonDTO armarDetalleCanon(UnidadDeVenta unidad, Festival festival) {
        // La relación puede venir como proxy de Hibernate: se obtiene la subclase real
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

    // Reporte del canon de la unidad del responsable logueado
    @Override
    @Transactional(readOnly = true)
    public CanonDTO calcularCanonUnidad(String nombreUsuario) {
        UnidadDeVenta unidad = getUdVByUser(nombreUsuario);
        if (unidad.getFestival() == null) {
            throw new IllegalArgumentException("Tu unidad de venta no tiene un festival asignado.");
        }
        return armarDetalleCanon(unidad, unidad.getFestival());
    }

    // Tipo de la unidad según su subclase
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

        // De mayor a menor canon; si empatan, por nombre comercial
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

    //Rentabilidad de un festival a la fecha actual
    public RentabilidadNetaDTO calcularRentabilidadNeta(UnidadDeVenta unidad, Festival festival){

        double totalVentas = unidad.getRecaudacionTotal();
        double costoPlatos = unidad.getCostoTotal();
        double totalSueldos = calcularTotalSueldos(unidad, festival);
        double canon = calcularCanon(unidad, festival);

        double rentabilidadNeta = (totalVentas - costoPlatos) - totalSueldos - canon;

        return new RentabilidadNetaDTO(totalVentas, costoPlatos, totalSueldos, canon, rentabilidadNeta);
    }

}
