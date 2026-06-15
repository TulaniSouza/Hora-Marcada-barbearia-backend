package dev.guilhermesilva.atom_backend.controller;

import dev.guilhermesilva.atom_backend.dto.request.ServiceTypeRequest;
import dev.guilhermesilva.atom_backend.dto.response.ServiceTypeResponse;
import dev.guilhermesilva.atom_backend.service.ServiceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-types")
@RequiredArgsConstructor
@Tag(name = "Tipos de Serviço", description = "Gerenciamento dos tipos de serviços oferecidos")
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    @Operation(summary = "Cria um novo tipo de serviço")
    @PostMapping
    public ResponseEntity<ServiceTypeResponse> create(
            @Valid @RequestBody ServiceTypeRequest request
    ) {
        ServiceTypeResponse response = serviceTypeService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Lista todos os tipos de serviço")
    @GetMapping
    public ResponseEntity<List<ServiceTypeResponse>> findAll() {
        List<ServiceTypeResponse> response = serviceTypeService.findAll();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista todos os tipos de serviço ativos")
    @GetMapping("/active")
    public ResponseEntity<List<ServiceTypeResponse>> findAllActive() {
        List<ServiceTypeResponse> response = serviceTypeService.findAllActive();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca um tipo de serviço pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceTypeResponse> findById(
            @PathVariable Long id
    ) {
        ServiceTypeResponse response = serviceTypeService.findById(id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualiza um tipo de serviço pelo ID")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceTypeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceTypeRequest request
    ) {
        ServiceTypeResponse response = serviceTypeService.update(id, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desativa um tipo de serviço pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id
    ) {
        serviceTypeService.deactivate(id);

        return ResponseEntity.noContent().build();
    }
}