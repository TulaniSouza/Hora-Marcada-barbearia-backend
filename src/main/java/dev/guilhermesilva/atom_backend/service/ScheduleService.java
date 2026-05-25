package dev.guilhermesilva.atom_backend.service;

import dev.guilhermesilva.atom_backend.dto.response.AppointmentResponse;
import dev.guilhermesilva.atom_backend.dto.response.AvailableTimeResponse;
import dev.guilhermesilva.atom_backend.dto.response.BarberScheduleResponse;
import dev.guilhermesilva.atom_backend.entity.Appointment;
import dev.guilhermesilva.atom_backend.entity.ServiceType;
import dev.guilhermesilva.atom_backend.enums.AppointmentStatus;
import dev.guilhermesilva.atom_backend.exception.BusinessException;
import dev.guilhermesilva.atom_backend.exception.ResourceNotFoundException;
import dev.guilhermesilva.atom_backend.mapper.AppointmentMapper;
import dev.guilhermesilva.atom_backend.repository.AppointmentRepository;
import dev.guilhermesilva.atom_backend.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);
    private static final int SLOT_INTERVAL_IN_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional(readOnly = true)
    public List<AvailableTimeResponse> findAvailableTimes(
            LocalDate date,
            Long serviceTypeId
    ) {
        validateBusinessDay(date);

        ServiceType serviceType = serviceTypeRepository.findByIdAndActiveTrue(serviceTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active service type not found with id: " + serviceTypeId
                ));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Appointment> scheduledAppointments = appointmentRepository
                .findByStatusAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
                        AppointmentStatus.AGENDADO,
                        startOfDay,
                        endOfDay
                );

        return generateAvailableTimes(
                date,
                serviceType,
                scheduledAppointments
        );
    }

    @Transactional(readOnly = true)
    public BarberScheduleResponse findBarberSchedule(
            LocalDate date,
            AppointmentStatus status
    ) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments;

        if (status == null) {
            appointments = appointmentRepository
                    .findByAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
                            startOfDay,
                            endOfDay
                    );
        } else {
            appointments = appointmentRepository
                    .findByAppointmentDateTimeBetweenAndStatusOrderByAppointmentDateTimeAsc(
                            startOfDay,
                            endOfDay,
                            status
                    );
        }

        List<AppointmentResponse> appointmentResponses = appointments
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();

        return BarberScheduleResponse.builder()
                .date(date)
                .totalAppointments(appointments.size())
                .scheduledAppointments(countByStatus(appointments, AppointmentStatus.AGENDADO))
                .cancelledAppointments(countByStatus(appointments, AppointmentStatus.CANCELADO))
                .completedAppointments(countByStatus(appointments, AppointmentStatus.FINALIZADO))
                .appointments(appointmentResponses)
                .build();
    }

    private List<AvailableTimeResponse> generateAvailableTimes(
            LocalDate date,
            ServiceType serviceType,
            List<Appointment> scheduledAppointments
    ) {
        LocalTime lastPossibleStartTime = CLOSING_TIME.minusMinutes(
                serviceType.getDurationInMinutes()
        );

        return generateTimeSlots(
                date,
                lastPossibleStartTime,
                serviceType,
                scheduledAppointments
        );
    }

    private List<AvailableTimeResponse> generateTimeSlots(
            LocalDate date,
            LocalTime lastPossibleStartTime,
            ServiceType serviceType,
            List<Appointment> scheduledAppointments
    ) {
        List<AvailableTimeResponse> availableTimes = new ArrayList<>();

        LocalTime currentTime = OPENING_TIME;

        while (!currentTime.isAfter(lastPossibleStartTime)) {
            LocalDateTime slotStart = LocalDateTime.of(date, currentTime);
            LocalDateTime slotEnd = slotStart.plusMinutes(serviceType.getDurationInMinutes());

            boolean available = isSlotAvailable(
                    slotStart,
                    slotEnd,
                    scheduledAppointments
            );

            availableTimes.add(
                    AvailableTimeResponse.builder()
                            .time(currentTime)
                            .available(available)
                            .build()
            );

            currentTime = currentTime.plusMinutes(SLOT_INTERVAL_IN_MINUTES);
        }

        return availableTimes;
    }

    private boolean isSlotAvailable(
            LocalDateTime slotStart,
            LocalDateTime slotEnd,
            List<Appointment> scheduledAppointments
    ) {
        if (slotStart.isBefore(LocalDateTime.now())) {
            return false;
        }

        return scheduledAppointments
                .stream()
                .noneMatch(existingAppointment -> {
                    LocalDateTime existingStart = existingAppointment.getAppointmentDateTime();
                    LocalDateTime existingEnd = existingStart.plusMinutes(
                            existingAppointment.getServiceType().getDurationInMinutes()
                    );

                    return slotStart.isBefore(existingEnd)
                            && slotEnd.isAfter(existingStart);
                });
    }

    private void validateBusinessDay(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new BusinessException("The barbershop is closed on Sundays");
        }
    }

    private Integer countByStatus(
            List<Appointment> appointments,
            AppointmentStatus status
    ) {
        return Math.toIntExact(
                appointments.stream()
                        .filter(appointment -> appointment.getStatus() == status)
                        .count()
        );
    }
}