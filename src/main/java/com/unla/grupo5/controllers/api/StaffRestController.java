package com.unla.grupo5.controllers.api;

import com.unla.grupo5.dtos.StaffCreateRequestDTO;
import com.unla.grupo5.dtos.StaffResponseDTO;
import com.unla.grupo5.services.EmpleadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@Tag(name = "Staff", description = "Gestión REST de empleados (Cocineros y Cajeros) de Epicentro Gourmet")
public class StaffRestController {

    @Autowired
    private EmpleadoService empleadoService;

    @Operation(
            summary = "Registrar un nuevo empleado",
            description = "Crea un Cocinero o Cajero validando mayoría de edad, DNI único y asignación a una Unidad de Venta activa, generando automáticamente su usuario de acceso."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Empleado registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StaffResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o regla de negocio incumplida (DNI duplicado, menor de edad, falta categoría o turno)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Unidad de Venta no encontrada",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    public ResponseEntity<StaffResponseDTO> registrarEmpleado(@Valid @RequestBody StaffCreateRequestDTO dto) {
        StaffResponseDTO creado = empleadoService.registrarEmpleadoApi(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(
            summary = "Buscar empleados con filtros opcionales",
            description = "Permite filtrar empleados por nombre, apellido, tipo de empleado (COCINERO / CAJERO), unidad de venta y estado activo. Si no se envían filtros, retorna el listado completo."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de empleados obtenido correctamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StaffResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetro tipoEmpleado inválido (debe ser COCINERO o CAJERO)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<StaffResponseDTO>> buscarEmpleados(
            @Parameter(description = "Coincidencia parcial por nombre (insensible a mayúsculas)", example = "Carla")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Coincidencia parcial por apellido (insensible a mayúsculas)", example = "Dev")
            @RequestParam(required = false) String apellido,
            @Parameter(description = "Rol específico del empleado: COCINERO o CAJERO", example = "CAJERO")
            @RequestParam(required = false) String tipoEmpleado,
            @Parameter(description = "ID de la Unidad de Venta asignada", example = "1")
            @RequestParam(required = false) Long idUnidadDeVenta,
            @Parameter(description = "Estado lógico del empleado (true = activo, false = inactivo)", example = "true")
            @RequestParam(required = false) Boolean activo) {

        List<StaffResponseDTO> resultados = empleadoService.buscarEmpleadosApi(
                nombre, apellido, tipoEmpleado, idUnidadDeVenta, activo);
        return ResponseEntity.ok(resultados);
    }

    @Operation(
            summary = "Obtener empleado por ID",
            description = "Devuelve el detalle de un empleado específico a partir de su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Empleado encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StaffResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe ningún empleado con el ID especificado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> obtenerPorId(
            @Parameter(description = "ID único del empleado", example = "10", required = true)
            @PathVariable Long id) {
        StaffResponseDTO empleado = empleadoService.obtenerEmpleadoPorIdApi(id);
        return ResponseEntity.ok(empleado);
    }

}