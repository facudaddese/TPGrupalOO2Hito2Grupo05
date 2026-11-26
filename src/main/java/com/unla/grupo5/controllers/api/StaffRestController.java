package com.unla.grupo5.controllers.api;

import com.unla.grupo5.dtos.StaffCreateRequestDTO;
import com.unla.grupo5.dtos.StaffResponseDTO;
import com.unla.grupo5.services.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffRestController {

    @Autowired
    private EmpleadoService empleadoService;

    @PostMapping
    public ResponseEntity<StaffResponseDTO> registrarEmpleado(@Valid @RequestBody StaffCreateRequestDTO dto) {
        StaffResponseDTO creado = empleadoService.registrarEmpleadoApi(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<StaffResponseDTO>> buscarEmpleados(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String tipoEmpleado,
            @RequestParam(required = false) Long idUnidadDeVenta,
            @RequestParam(required = false) Boolean activo) {

        List<StaffResponseDTO> resultados = empleadoService.buscarEmpleadosApi(
                nombre, apellido, tipoEmpleado, idUnidadDeVenta, activo);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> obtenerPorId(@PathVariable Long id) {
        StaffResponseDTO empleado = empleadoService.obtenerEmpleadoPorIdApi(id);
        return ResponseEntity.ok(empleado);
    }

}