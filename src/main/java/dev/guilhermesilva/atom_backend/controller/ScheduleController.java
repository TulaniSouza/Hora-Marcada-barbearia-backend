package dev.guilhermesilva.atom_backend.controller;

import dev.guilhermesilva.atom_backend.dto.response.AvailableTimeResponse;
import dev.guilhermesilva.atom_backend.dto.response.BarberScheduleResponse;
import dev.guilhermesilva.atom_backend.enums.AppointmentStatus;
import dev.guilhermesilva.atom_backend.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor

@Tag(name = "Horarios", description = "Horários disponíveis para agendamento")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "Horários disponíveis")
    @GetMapping("/appointments/available-times")
    public ResponseEntity<List<AvailableTimeResponse>> findAvailableTimes(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam
            Long serviceTypeId
    ) {
        List<AvailableTimeResponse> response = scheduleService.findAvailableTimes(
                date,
                serviceTypeId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Agenda por data.")
    @GetMapping("/barber/schedule")
    public ResponseEntity<BarberScheduleResponse> findBarberSchedule(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(required = false)
            AppointmentStatus status
    ) {
        BarberScheduleResponse response = scheduleService.findBarberSchedule(
                date,
                status
        );

        return ResponseEntity.ok(response);
    }
}