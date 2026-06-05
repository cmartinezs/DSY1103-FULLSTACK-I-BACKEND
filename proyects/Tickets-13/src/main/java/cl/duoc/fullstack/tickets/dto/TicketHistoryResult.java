package cl.duoc.fullstack.tickets.dto;

import java.time.LocalDateTime;

public record TicketHistoryResult(
    Long id,
    String previousStatus,
    String newStatus,
    String previousAssignedEmail,
    String newAssignedEmail,
    LocalDateTime changedAt,
    String comment
) {}
