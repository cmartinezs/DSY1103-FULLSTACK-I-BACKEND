package cl.duoc.fullstack.tickets.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record TicketRequest(
    @NotBlank(message = "El titulo es requerido")
    @Size(min = 1, max = 50)
    String title,
    @NotBlank(message = "La descripcion es requerida")
    String description,
    String status,
    LocalDateTime effectiveResolutionDate,
    @NotBlank(message = "El email del creador es requerido")
    @Email(message = "El email no tiene un formato valido")
    String createdByEmail
) {}
