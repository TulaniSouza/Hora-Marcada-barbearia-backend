package dev.guilhermesilva.atom_backend.service;

import dev.guilhermesilva.atom_backend.dto.request.AppointmentRequest;
import dev.guilhermesilva.atom_backend.dto.response.AppointmentResponse;
import dev.guilhermesilva.atom_backend.entity.Appointment;
import dev.guilhermesilva.atom_backend.entity.ServiceType;
import dev.guilhermesilva.atom_backend.enums.AppointmentStatus;
import dev.guilhermesilva.atom_backend.exception.BusinessException;
import dev.guilhermesilva.atom_backend.exception.ResourceNotFoundException; // Adicionado para garantir a compilação
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final AppointmentMapper appointmentMapper;

    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);

    private static final int SLOT_INTERVAL_IN_MINUTES = 30;

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        ServiceType serviceType = findActiveServiceTypeById(request.getServiceTypeId());

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                request.getAppointmentDate(),
                request.getAppointmentTime()
        );

        validateBusinessDay(request.getAppointmentDate());
        validateAppointmentIsInFuture(appointmentDateTime);
        validateAppointmentTimeSlot(appointmentDateTime);
        validateAppointmentWithinBusinessHours(appointmentDateTime, serviceType);
        validateAppointmentAvailability(appointmentDateTime, serviceType);

        Appointment appointment = appointmentMapper.toEntity(
                request,
                serviceType,
                appointmentDateTime
        );

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return appointmentRepository
                .findByAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(startOfDay, endOfDay)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findScheduledByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return appointmentRepository
                .findByStatusAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
                        AppointmentStatus.AGENDADO,
                        startOfDay,
                        endOfDay
                )
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Transactional
    public AppointmentResponse cancel(Long id) {
        Appointment appointment = findAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELADO) {
            throw new BusinessException("Este agendamento já está cancelado.");
        }

        if (appointment.getStatus() == AppointmentStatus.FINALIZADO) {
            throw new BusinessException("Um agendamento concluído não pode ser cancelado.");
        }

        appointment.setStatus(AppointmentStatus.CANCELADO);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(updatedAppointment);
    }

    @Transactional
    public AppointmentResponse complete(Long id) {
        Appointment appointment = findAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELADO) {
            throw new BusinessException("Um agendamento cancelado não pode ser concluído.");
        }

        if (appointment.getStatus() == AppointmentStatus.FINALIZADO) {
            throw new BusinessException("Este agendamento já está concluído.");
        }

        appointment.setStatus(AppointmentStatus.FINALIZADO);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(updatedAppointment);
    }

    private ServiceType findActiveServiceTypeById(Long serviceTypeId) {
        return serviceTypeRepository.findByIdAndActiveTrue(serviceTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de serviço ativo não encontrado com o ID: " + serviceTypeId
                ));
    }

    private Appointment findAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Agendamento não encontrado com o ID: " + id
                ));
    }

    private void validateAppointmentIsInFuture(LocalDateTime appointmentDateTime) {
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("A data e hora do agendamento devem ser no futuro.");
        }
    }

    private void validateAppointmentAvailability(
            LocalDateTime newAppointmentStart,
            ServiceType newServiceType
    ) {
        LocalDateTime startOfDay = newAppointmentStart.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = newAppointmentStart.toLocalDate().plusDays(1).atStartOfDay();

        List<Appointment> scheduledAppointments = appointmentRepository
                .findByStatusAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
                        AppointmentStatus.AGENDADO,
                        startOfDay,
                        endOfDay
                );

        LocalDateTime newAppointmentEnd = newAppointmentStart.plusMinutes(
                newServiceType.getDurationInMinutes()
        );

        boolean hasConflict = scheduledAppointments.stream()
                .anyMatch(existingAppointment -> {
                    LocalDateTime existingStart = existingAppointment.getAppointmentDateTime();
                    LocalDateTime existingEnd = existingStart.plusMinutes(
                            existingAppointment.getServiceType().getDurationInMinutes()
                    );

                    return newAppointmentStart.isBefore(existingEnd)
                            && newAppointmentEnd.isAfter(existingStart);
                });

        if (hasConflict) {
            throw new BusinessException("Já existe um agendamento marcado para este horário.");
        }
    }
    private void validateBusinessDay(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new BusinessException("Barbearia está fechada aos domingos.");
        }
    }

    private void validateAppointmentWithinBusinessHours(
            LocalDateTime appointmentDateTime,
            ServiceType serviceType
    ) {
        LocalTime appointmentStartTime = appointmentDateTime.toLocalTime();
        LocalTime appointmentEndTime = appointmentStartTime.plusMinutes(
                serviceType.getDurationInMinutes()
        );

        if (appointmentStartTime.isBefore(OPENING_TIME)) {
            throw new BusinessException("Appointment time is before opening time");
        }

        if (appointmentEndTime.isAfter(CLOSING_TIME)) {
            throw new BusinessException("Appointment time exceeds closing time");
        }
    }
    private void validateAppointmentTimeSlot(LocalDateTime appointmentDateTime) {
        int minute = appointmentDateTime.getMinute();

        if (minute % SLOT_INTERVAL_IN_MINUTES != 0) {
            throw new BusinessException("Appointment time must follow the configured time slot interval");
        }
    }

}