package dev.guilhermesilva.atom_backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BarberResponse {

    private Long id;

    private String name;

    private String email;

    private Boolean active;
}