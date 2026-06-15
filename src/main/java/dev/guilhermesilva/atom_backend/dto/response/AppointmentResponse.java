package dev.guilhermesilva.atom_backend.dto.response;

import dev.guilhermesilva.atom_backend.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class AppointmentResponse {

    private Long id;

    private Long customerId;

    private String customerName;

    private String customerPhone;

    private Long barberId;

    private String barberName;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private LocalDateTime appointmentDateTime;

    private AppointmentStatus status;

    private Long serviceTypeId;

    private String serviceTypeName;

    private BigDecimal servicePrice;

    private Integer serviceDurationInMinutes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}