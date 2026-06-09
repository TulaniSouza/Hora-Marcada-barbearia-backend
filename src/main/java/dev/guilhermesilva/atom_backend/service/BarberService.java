package dev.guilhermesilva.atom_backend.service;

import dev.guilhermesilva.atom_backend.dto.response.BarberResponse;
import dev.guilhermesilva.atom_backend.repository.BarberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BarberService {

    private final BarberRepository barberRepository;

    @Transactional(readOnly = true)
    public List<BarberResponse> findAll() {
        return barberRepository.findAll()
                .stream()
                .map(barber -> BarberResponse.builder()
                        .id(barber.getId())
                        .name(barber.getName())
                        .email(barber.getEmail())
                        .active(barber.getActive())
                        .build())
                .toList();
    }
}