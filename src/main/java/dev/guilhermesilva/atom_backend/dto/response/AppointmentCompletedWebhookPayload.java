package dev.guilhermesilva.atom_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCompletedWebhookPayload {

    private Long idCliente;
    private String nomeCliente;
    private String telefoneCliente;
    private Long idAgendamento;
}
