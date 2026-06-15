package dev.guilhermesilva.atom_backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class AvailableTimeResponse {

    private LocalTime time;

    private Boolean available;
}