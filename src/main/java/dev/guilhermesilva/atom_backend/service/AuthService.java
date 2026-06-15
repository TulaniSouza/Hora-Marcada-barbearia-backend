package dev.guilhermesilva.atom_backend.service;

import dev.guilhermesilva.atom_backend.dto.request.LoginRequest;
import dev.guilhermesilva.atom_backend.dto.request.RegisterRequest;
import dev.guilhermesilva.atom_backend.dto.response.AuthResponse;
import dev.guilhermesilva.atom_backend.entity.Barber;
import dev.guilhermesilva.atom_backend.exception.BusinessException;
import dev.guilhermesilva.atom_backend.repository.BarberRepository;
import dev.guilhermesilva.atom_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BarberRepository barberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateEmailDoesNotExist(request.getEmail());

        Barber barber = Barber.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .build();

        Barber savedBarber = barberRepository.save(barber);

        String token = jwtService.generateToken(savedBarber);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .barberId(savedBarber.getId())
                .name(savedBarber.getName())
                .email(savedBarber.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        Barber barber = barberRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        String token = jwtService.generateToken(barber);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .barberId(barber.getId())
                .name(barber.getName())
                .email(barber.getEmail())
                .build();
    }

    private void validateEmailDoesNotExist(String email) {
        boolean exists = barberRepository.existsByEmail(email.trim().toLowerCase());

        if (exists) {
            throw new BusinessException("Email already registered");
        }
    }
}