package dev.guilhermesilva.atom_backend.controller;


import dev.guilhermesilva.atom_backend.dto.response.ApiResponse;
import dev.guilhermesilva.atom_backend.dto.response.AvailableTimeResponse;
import dev.guilhermesilva.atom_backend.dto.response.BarberScheduleResponse;
import dev.guilhermesilva.atom_backend.enums.AppointmentStatus;
import dev.guilhermesilva.atom_backend.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

        @Operation(summary = "Buscar horários disponíveis para agendamento")
    @GetMapping("/appointments/available-times")
    public ResponseEntity<ApiResponse<List<AvailableTimeResponse>>> findAvailableTimes(
            @RequestParam Long barberId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam Long serviceTypeId
    ) {
        List<AvailableTimeResponse> response = scheduleService.findAvailableTimes(
                barberId,
                date,
                serviceTypeId
        );

        return ResponseEntity.ok(
                ApiResponse.success("Available times found successfully", response)
        );
    }

        @Operation(summary = "Buscar agenda do barbeiro autenticado por data")
    @GetMapping("/barber/schedule")
    public ResponseEntity<ApiResponse<BarberScheduleResponse>> findBarberSchedule(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(required = false)
            AppointmentStatus status
    ) {
        BarberScheduleResponse response = scheduleService.findAuthenticatedBarberSchedule(
                date,
                status
        );

        return ResponseEntity.ok(
                ApiResponse.success("Barber schedule found successfully", response)
        );
    }
}