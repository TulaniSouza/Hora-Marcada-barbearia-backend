package dev.guilhermesilva.atom_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReminderResponse {

    private String nomeCliente;
    private String telefoneCliente;
    private LocalDateTime dataHorario;
    private String nomeBarbeiro;
}
