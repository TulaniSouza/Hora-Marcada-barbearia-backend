package dev.guilhermesilva.atom_backend.service;

import dev.guilhermesilva.atom_backend.dto.request.AppointmentRequest;
import dev.guilhermesilva.atom_backend.dto.response.AppointmentCompletedWebhookPayload;
import dev.guilhermesilva.atom_backend.dto.response.AppointmentReminderResponse;
import dev.guilhermesilva.atom_backend.dto.response.AppointmentResponse;
import dev.guilhermesilva.atom_backend.entity.Appointment;
import dev.guilhermesilva.atom_backend.entity.Barber;
import dev.guilhermesilva.atom_backend.entity.Customer;
import dev.guilhermesilva.atom_backend.entity.ServiceType;
import dev.guilhermesilva.atom_backend.enums.AppointmentStatus;
import dev.guilhermesilva.atom_backend.exception.BusinessException;
import dev.guilhermesilva.atom_backend.exception.ResourceNotFoundException;
import dev.guilhermesilva.atom_backend.mapper.AppointmentMapper;
import dev.guilhermesilva.atom_backend.repository.AppointmentRepository;
import dev.guilhermesilva.atom_backend.repository.BarberRepository;
import dev.guilhermesilva.atom_backend.repository.CustomerRepository;
import dev.guilhermesilva.atom_backend.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);
    private static final int SLOT_INTERVAL_IN_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final CustomerRepository customerRepository;
    private final BarberRepository barberRepository;
    private final AppointmentMapper appointmentMapper;
    private final RestTemplate restTemplate;

    @Value("${N8N_WEBHOOK_URL:}")
    private String n8nWebhookUrl;

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        Customer customer = getAuthenticatedCustomer();

        Barber barber = findBarberById(request.getBarberId());

        ServiceType serviceType = findActiveServiceTypeById(request.getServiceTypeId());

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                request.getAppointmentDate(),
                request.getAppointmentTime()
        );

        validateBusinessDay(request.getAppointmentDate());
        validateAppointmentIsInFuture(appointmentDateTime);
        validateAppointmentTimeSlot(appointmentDateTime);
        validateAppointmentWithinBusinessHours(appointmentDateTime, serviceType);
        validateAppointmentAvailability(
                barber.getId(),
                appointmentDateTime,
                serviceType
        );

        Appointment appointment = appointmentMapper.toEntity(
                request,
                serviceType,
                customer,
                barber,
                appointmentDateTime
        );

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(savedAppointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> findAllPaginated(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> findByDatePaginated(
            LocalDate date,
            Pageable pageable
    ) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return appointmentRepository
                .findByAppointmentDateTimeBetween(startOfDay, endOfDay, pageable)
                .map(appointmentMapper::toResponse);
    }

    @Transactional
    public AppointmentResponse cancel(Long id) {
        Appointment appointment = findAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("Appointment is already cancelled");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Completed appointment cannot be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(updatedAppointment);
    }

    @Transactional
    public AppointmentResponse complete(Long id) {
        Appointment appointment = findAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("Cancelled appointment cannot be completed");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Appointment is already completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        if (updatedAppointment.getStatus() == AppointmentStatus.COMPLETED) {
            notifyCompletedAppointmentWebhook(updatedAppointment);
        }

        return appointmentMapper.toResponse(updatedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentReminderResponse> findRemindersForNext24Hours() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24);

        return appointmentRepository
                .findByStatusAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
                        AppointmentStatus.SCHEDULED,
                        now,
                        next24Hours
                )
                .stream()
                .map(appointment -> AppointmentReminderResponse.builder()
                        .nomeCliente(appointment.getCustomerName())
                        .telefoneCliente(appointment.getCustomerPhone())
                        .dataHorario(appointment.getAppointmentDateTime())
                        .nomeBarbeiro(appointment.getBarber().getName())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public long countCompletedAppointmentsByCustomer(Long customerId) {
        return appointmentRepository.countByCustomerIdAndStatus(customerId, AppointmentStatus.COMPLETED);
    }

    private void notifyCompletedAppointmentWebhook(Appointment appointment) {
        if (n8nWebhookUrl == null || n8nWebhookUrl.isBlank()) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                AppointmentCompletedWebhookPayload payload = AppointmentCompletedWebhookPayload.builder()
                        .idCliente(appointment.getCustomer().getId())
                        .nomeCliente(appointment.getCustomerName())
                        .telefoneCliente(appointment.getCustomerPhone())
                        .idAgendamento(appointment.getId())
                        .build();

                restTemplate.postForEntity(n8nWebhookUrl, payload, Void.class);
            } catch (Exception ex) {
                // Fallback silencioso para não quebrar o fluxo principal.
            }
        });
    }

    private Customer getAuthenticatedCustomer() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private Barber findBarberById(Long barberId) {
        return barberRepository.findById(barberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Barber not found with id: " + barberId
                ));
    }

    private ServiceType findActiveServiceTypeById(Long serviceTypeId) {
        return serviceTypeRepository.findByIdAndActiveTrue(serviceTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active service type not found with id: " + serviceTypeId
                ));
    }

    private Appointment findAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + id
                ));
    }

    private void validateBusinessDay(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new BusinessException("The barbershop is closed on Sundays");
        }
    }

    private void validateAppointmentIsInFuture(LocalDateTime appointmentDateTime) {
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Appointment date and time must be in the future");
        }
    }

    private void validateAppointmentTimeSlot(LocalDateTime appointmentDateTime) {
        int minute = appointmentDateTime.getMinute();

        if (minute % SLOT_INTERVAL_IN_MINUTES != 0) {
            throw new BusinessException("Appointment time must follow the configured time slot interval");
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

    private void validateAppointmentAvailability(
            Long barberId,
            LocalDateTime newAppointmentStart,
            ServiceType newServiceType
    ) {
        LocalDateTime startOfDay = newAppointmentStart.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = newAppointmentStart.toLocalDate().plusDays(1).atStartOfDay();

        List<Appointment> scheduledAppointments = appointmentRepository
                .findByBarberIdAndStatusAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
                        barberId,
                        AppointmentStatus.SCHEDULED,
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
            throw new BusinessException("There is already an appointment scheduled for this barber at this time");
        }
    }
}