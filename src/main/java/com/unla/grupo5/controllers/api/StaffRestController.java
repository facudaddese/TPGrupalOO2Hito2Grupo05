package com.unla.grupo5.controllers.api;

import com.unla.grupo5.dtos.StaffCreateRequestDTO;
import com.unla.grupo5.dtos.StaffResponseDTO;
import com.unla.grupo5.services.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}