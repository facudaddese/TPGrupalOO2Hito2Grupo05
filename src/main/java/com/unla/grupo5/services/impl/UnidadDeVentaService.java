package com.unla.grupo5.services.impl;

import com.unla.grupo5.dtos.ReporteVentaDTO;
import com.unla.grupo5.dtos.UnidadDeVentaModificarDTO;
import com.unla.grupo5.dtos.UnidadDeVentaRequestDTO;
import com.unla.grupo5.entities.Festival;
import com.unla.grupo5.entities.Rol;
import com.unla.grupo5.entities.UnidadDeVenta;
import com.unla.grupo5.entities.Usuario;
import com.unla.grupo5.repositories.FestivalRepository;
import com.unla.grupo5.repositories.RolRepository;
import com.unla.grupo5.repositories.UnidadDeVentaRepository;
import com.unla.grupo5.repositories.UsuarioRepository;
import com.unla.grupo5.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class UnidadDeVentaService implements com.unla.grupo5.services.UnidadDeVentaService {

    // Rol que convierte al usuario creado en el responsable de la unidad
    private static final String ROL_RESPONSABLE = "RESPONSABLE";
    // Únicos roles que se aceptan desde el formulario
    private static final List<String> ROLES_VALIDOS = List.of("RESPONSABLE", "ADMINISTRADOR", "EMPLEADO");

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

        // El rol solo puede ser uno de los permitidos
        if (!ROLES_VALIDOS.contains(dto.getRol())) {
            throw new IllegalArgumentException("El rol seleccionado no es válido.");
        }

        // El nombre de usuario es único en todo el sistema
        // El e-mail es el nombre de usuario, y es único en todo el sistema
        if (usuarioRepository.existsByNombreUsuario(dto.getEmail())) {
            throw new IllegalArgumentException("El e-mail " + dto.getEmail() + " ya está en uso como usuario.");
        }
    }

    // Alta de una unidad de venta junto con su usuario.
    // Todo o nada: si algo falla no queda ni el usuario ni la unidad.
    @Override
    @Transactional
    public UnidadDeVenta registrarUnidad(UnidadDeVentaRequestDTO dto) {
        // 1. El festival elegido tiene que existir
        Festival festival = festivalRepository.findById(dto.getIdFestival())
                .orElseThrow(() -> new IllegalArgumentException("El festival seleccionado no existe."));

        // 2. Validaciones de negocio (código, rol y usuario)
        validarDatos(dto);

        // 3. Busca el rol en la tabla; si todavía no existe lo crea
        String nombreRol = dto.getRol();
        Rol rol = rolRepository.findByRol(nombreRol)
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setRol(nombreRol);
                    return rolRepository.save(r);
                });

        // 4. Se guarda primero la unidad, así el usuario puede quedar asociado a ella
        UnidadDeVenta unidad = new UnidadDeVenta();
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

        // 6. Solo si el rol es RESPONSABLE, ese usuario pasa a ser el responsable de la unidad
        if (ROL_RESPONSABLE.equals(nombreRol)) {
            unidad.setResponsable(usuario);
            unidad = unidadDeVentaRepository.save(unidad);
        }

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
}
