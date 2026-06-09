package dev.guilhermesilva.atom_backend.repository;

import dev.guilhermesilva.atom_backend.entity.Appointment;
import dev.guilhermesilva.atom_backend.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByBarberIdAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
            Long barberId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Appointment> findByBarberIdAndStatusAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
            Long barberId,
            AppointmentStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    Page<Appointment> findByAppointmentDateTimeBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Page<Appointment> findByBarberIdAndAppointmentDateTimeBetween(
            Long barberId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}