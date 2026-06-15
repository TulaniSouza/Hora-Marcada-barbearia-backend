package dev.guilhermesilva.atom_backend.controller;

import dev.guilhermesilva.atom_backend.dto.response.ApiResponse;
import dev.guilhermesilva.atom_backend.dto.response.BarberResponse;
import dev.guilhermesilva.atom_backend.service.BarberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/barbers")
@RestController
@RequiredArgsConstructor
@Tag(name = "Barbeiros", description = "Gerenciamento da lista de barbeiros")
public class BarberController {

    private final BarberService barberService;

    @Operation(summary = "Listar todos os barbeiros")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BarberResponse>>> findAll() {
        List<BarberResponse> response = barberService.findAll();

        return ResponseEntity.ok(
                ApiResponse.success("Barbeiros não encontrados", response)
        );
    }
}