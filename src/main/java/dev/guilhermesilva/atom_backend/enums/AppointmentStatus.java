package dev.guilhermesilva.atom_backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentStatus {

    SCHEDULED("AGENDADO"),
    CANCELLED("CANCELADO"),
    COMPLETED("FINALIZADO");

    private final String value;

    AppointmentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AppointmentStatus fromString(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim().toUpperCase();

        for (AppointmentStatus status : AppointmentStatus.values()) {
            if (status.name().equalsIgnoreCase(normalizedValue)
                    || status.value.equalsIgnoreCase(normalizedValue)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown AppointmentStatus: " + value);
    }
}