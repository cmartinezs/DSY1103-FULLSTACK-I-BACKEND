package cl.duoc.fullstack.tickets.dto;

import java.time.LocalDateTime;

public record TicketCommand(
    String title,
    String description,
    String status,
    LocalDateTime effectiveResolutionDate,
    String createdByEmail
) {}