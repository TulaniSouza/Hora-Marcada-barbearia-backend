package dev.guilhermesilva.atom_backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ServiceTypeResponse {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer durationInMinutes;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}