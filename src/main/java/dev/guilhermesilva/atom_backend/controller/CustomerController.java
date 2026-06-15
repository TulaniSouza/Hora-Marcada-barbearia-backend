package dev.guilhermesilva.atom_backend.controller;

import dev.guilhermesilva.atom_backend.dto.response.ApiResponse;
import dev.guilhermesilva.atom_backend.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/clientes")
@RestController
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Consulta de clientes e fidelidade")
public class CustomerController {

    private final AppointmentService appointmentService;

    @Operation(summary = "Contar quantos cortes concluídos um cliente possui")
    @GetMapping("/{id}/quantidade-cortes")
    public ResponseEntity<ApiResponse<Long>> countCompletedAppointments(@PathVariable Long id) {
        long quantity = appointmentService.countCompletedAppointmentsByCustomer(id);

        return ResponseEntity.ok(
                ApiResponse.success("Completed appointments counted successfully", quantity)
        );
    }
}
