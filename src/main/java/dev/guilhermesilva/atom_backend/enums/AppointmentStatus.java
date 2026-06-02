package dev.guilhermesilva.atom_backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentStatus {
    SCHEDULED,
    CANCELLED,
    COMPLETED;

    @JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static AppointmentStatus fromString(String v) {
        if (v == null) {
            return null;
        }
        return AppointmentStatus.valueOf(v.trim().toUpperCase());
    }
}
