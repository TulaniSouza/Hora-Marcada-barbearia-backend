package dev.guilhermesilva.atom_backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String token;

    private String tokenType;

    private Long barberId;

    private String name;

    private String email;
}