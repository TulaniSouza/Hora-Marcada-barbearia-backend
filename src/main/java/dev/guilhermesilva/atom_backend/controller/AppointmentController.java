package dev.guilhermesilva.atom_backend.controller;

import dev.guilhermesilva.atom_backend.dto.request.AppointmentRequest;
import dev.guilhermesilva.atom_backend.dto.response.ApiResponse;
import dev.guilhermesilva.atom_backend.dto.response.AppointmentReminderResponse;
import dev.guilhermesilva.atom_backend.dto.response.AppointmentResponse;
import dev.guilhermesilva.atom_backend.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping({"/api/appointments", "/api/v1/agendamentos"})
@RestController
@RequiredArgsConstructor
@Tag(name = "Agendamentos", description = "Gerenciamento de agendamentos")
public class AppointmentController {

    private final AppointmentService appointmentService;

        @Operation(summary = "Criar um novo agendamento")
    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> create(
            @Valid @RequestBody AppointmentRequest request
    ) {
        AppointmentResponse response = appointmentService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment created successfully", response));
    }

        @Operation(summary = "Listar agendamentos com paginação")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AppointmentResponse>>> findAll(
            Pageable pageable
    ) {
        Page<AppointmentResponse> response = appointmentService.findAllPaginated(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Appointments found successfully", response)
        );
    }

        @Operation(summary = "Listar agendamentos por data com paginação")
    @GetMapping("/date")
    public ResponseEntity<ApiResponse<Page<AppointmentResponse>>> findByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Pageable pageable
    ) {
        Page<AppointmentResponse> response = appointmentService.findByDatePaginated(
                date,
                pageable
        );

        return ResponseEntity.ok(
                ApiResponse.success("Appointments found successfully", response)
        );
    }

        @Operation(summary = "Cancelar agendamento por ID")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancel(
            @PathVariable Long id
    ) {
        AppointmentResponse response = appointmentService.cancel(id);

        return ResponseEntity.ok(
                ApiResponse.success("Appointment cancelled successfully", response)
        );
    }

        @Operation(summary = "Concluir agendamento por ID")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<AppointmentResponse>> complete(
            @PathVariable Long id
    ) {
        AppointmentResponse response = appointmentService.complete(id);

        return ResponseEntity.ok(
                ApiResponse.success("Appointment completed successfully", response)
        );
    }

    @Operation(summary = "Listar lembretes dos próximos 24h para integração com n8n")
    @GetMapping("/lembretes")
    public ResponseEntity<ApiResponse<List<AppointmentReminderResponse>>> findRemindersForNext24Hours() {
        List<AppointmentReminderResponse> response = appointmentService.findRemindersForNext24Hours();

        return ResponseEntity.ok(
                ApiResponse.success("Reminders found successfully", response)
        );
    }
}