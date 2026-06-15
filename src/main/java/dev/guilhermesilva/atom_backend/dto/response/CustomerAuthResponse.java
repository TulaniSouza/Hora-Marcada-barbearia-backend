package dev.guilhermesilva.atom_backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerAuthResponse {

    private String token;

    private String tokenType;

    private Long customerId;

    private String name;

    private String phone;

    private String email;
}