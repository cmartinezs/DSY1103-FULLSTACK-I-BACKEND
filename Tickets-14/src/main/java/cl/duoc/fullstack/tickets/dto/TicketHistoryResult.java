package cl.duoc.fullstack.tickets.dto;

import java.time.LocalDateTime;

public record TicketHistoryResult(
    Long id,
    String previousStatus,
    String newStatus,
    LocalDateTime changedAt,
    String comment
) {}