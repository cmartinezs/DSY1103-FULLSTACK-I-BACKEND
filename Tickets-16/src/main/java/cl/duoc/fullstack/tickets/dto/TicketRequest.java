package cl.duoc.fullstack.tickets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record TicketRequest(
    @NotBlank(message = "El titulo es requerido")
    @Size(min = 1, max = 50)
    String title,
    @NotBlank(message = "La descripción es requerida")
    String description,
    String createdByName,
    Long assignedToId,
    Long categoryId,
    List<Long> tagIds,
    String status,
    LocalDateTime effectiveResolutionDate
) {}