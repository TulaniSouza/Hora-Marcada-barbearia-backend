package dev.guilhermesilva.atom_backend.repository;

import dev.guilhermesilva.atom_backend.entity.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BarberRepository extends JpaRepository<Barber, Long> {

    Optional<Barber> findByEmail(String email);

    boolean existsByEmail(String email);
}