package dev.guilhermesilva.atom_backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class BarberScheduleResponse {

    private LocalDate date;

    private Integer totalAppointments;

    private Integer scheduledAppointments;

    private Integer cancelledAppointments;

    private Integer completedAppointments;

    private List<AppointmentResponse> appointments;
}