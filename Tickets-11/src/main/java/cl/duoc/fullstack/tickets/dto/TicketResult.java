package cl.duoc.fullstack.tickets.dto;

import cl.duoc.fullstack.tickets.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TicketResult(
    Long id,
    String title,
    String description,
    String status,
    LocalDateTime createdAt,
    LocalDate estimatedResolutionDate,
    LocalDateTime effectiveResolutionDate,
    User createdBy,
    User assignedTo
) {}