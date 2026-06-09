package dev.guilhermesilva.atom_backend.service;

import dev.guilhermesilva.atom_backend.dto.request.CustomerLoginRequest;
import dev.guilhermesilva.atom_backend.dto.request.CustomerRegisterRequest;
import dev.guilhermesilva.atom_backend.dto.response.CustomerAuthResponse;
import dev.guilhermesilva.atom_backend.entity.Customer;
import dev.guilhermesilva.atom_backend.exception.BusinessException;
import dev.guilhermesilva.atom_backend.repository.CustomerRepository;
import dev.guilhermesilva.atom_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerAuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public CustomerAuthResponse register(CustomerRegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String phone = request.getPhone().trim();

        validateEmailDoesNotExist(email);
        validatePhoneDoesNotExist(phone);

        Customer customer = Customer.builder()
                .name(request.getName().trim())
                .phone(phone)
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        String token = jwtService.generateToken(savedCustomer);

        return CustomerAuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .customerId(savedCustomer.getId())
                .name(savedCustomer.getName())
                .phone(savedCustomer.getPhone())
                .email(savedCustomer.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public CustomerAuthResponse login(CustomerLoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.getPassword()
                )
        );

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        String token = jwtService.generateToken(customer);

        return CustomerAuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .customerId(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .build();
    }

    private void validateEmailDoesNotExist(String email) {
        if (customerRepository.existsByEmail(email)) {
            throw new BusinessException("Email already registered");
        }
    }

    private void validatePhoneDoesNotExist(String phone) {
        if (customerRepository.existsByPhone(phone)) {
            throw new BusinessException("Phone already registered");
        }
    }
}