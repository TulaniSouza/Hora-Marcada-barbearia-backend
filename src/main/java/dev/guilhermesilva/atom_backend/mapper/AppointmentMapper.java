package dev.guilhermesilva.atom_backend.mapper;

import dev.guilhermesilva.atom_backend.dto.request.AppointmentRequest;
import dev.guilhermesilva.atom_backend.dto.response.AppointmentResponse;
import dev.guilhermesilva.atom_backend.entity.Appointment;
import dev.guilhermesilva.atom_backend.entity.ServiceType;
import dev.guilhermesilva.atom_backend.enums.AppointmentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AppointmentMapper {

    public Appointment toEntity(
            AppointmentRequest request,
            ServiceType serviceType,
            LocalDateTime appointmentDateTime
    ) {
        return Appointment.builder()
                .customerName(request.getCustomerName().trim())
                .customerPhone(request.getCustomerPhone().trim())
                .appointmentDateTime(appointmentDateTime)
                .serviceType(serviceType)
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .customerName(appointment.getCustomerName())
                .customerPhone(appointment.getCustomerPhone())
                .appointmentDate(appointment.getAppointmentDateTime().toLocalDate())
                .appointmentTime(appointment.getAppointmentDateTime().toLocalTime())
                .appointmentDateTime(appointment.getAppointmentDateTime())
                .status(appointment.getStatus())
                .serviceTypeId(appointment.getServiceType().getId())
                .serviceTypeName(appointment.getServiceType().getName())
                .servicePrice(appointment.getServiceType().getPrice())
                .serviceDurationInMinutes(appointment.getServiceType().getDurationInMinutes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}