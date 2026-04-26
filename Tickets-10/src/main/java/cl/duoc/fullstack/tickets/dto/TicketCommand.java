package cl.duoc.fullstack.tickets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record TicketCommand(
    String title,
    String description,
    String status,
    LocalDateTime effectiveResolutionDate
) {}