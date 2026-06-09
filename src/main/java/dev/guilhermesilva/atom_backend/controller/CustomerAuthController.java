package dev.guilhermesilva.atom_backend.controller;

import dev.guilhermesilva.atom_backend.dto.request.CustomerLoginRequest;
import dev.guilhermesilva.atom_backend.dto.request.CustomerRegisterRequest;
import dev.guilhermesilva.atom_backend.dto.response.ApiResponse;
import dev.guilhermesilva.atom_backend.dto.response.CustomerAuthResponse;
import dev.guilhermesilva.atom_backend.service.CustomerAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/customers/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Registro e autenticação de clientes")
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;

        @Operation(summary = "Cadastrar um novo cliente")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerAuthResponse>> register(
            @Valid @RequestBody CustomerRegisterRequest request
    ) {
        CustomerAuthResponse response = customerAuthService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer registered successfully", response));
    }

        @Operation(summary = "Autenticar cliente")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<CustomerAuthResponse>> login(
            @Valid @RequestBody CustomerLoginRequest request
    ) {
        CustomerAuthResponse response = customerAuthService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("Customer logged in successfully", response)
        );
    }
}