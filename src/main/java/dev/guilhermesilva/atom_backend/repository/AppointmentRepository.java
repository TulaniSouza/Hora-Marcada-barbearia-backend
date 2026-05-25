package dev.guilhermesilva.atom_backend.repository;

import dev.guilhermesilva.atom_backend.entity.Appointment;
import dev.guilhermesilva.atom_backend.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByAppointmentDateTimeAndStatus(
            LocalDateTime appointmentDateTime,
            AppointmentStatus status
    );

    List<Appointment> findByAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Appointment> findByStatusOrderByAppointmentDateTimeAsc(
            AppointmentStatus status
    );

    List<Appointment> findByStatusAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
            AppointmentStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Appointment> findByAppointmentDateTimeBetweenAndStatusOrderByAppointmentDateTimeAsc(
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus status
    );
}