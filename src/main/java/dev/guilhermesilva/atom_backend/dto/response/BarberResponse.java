package dev.guilhermesilva.atom_backend.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarberResponse {

    private Long id;

    private String name;

    private String email;

    private Boolean active;
}