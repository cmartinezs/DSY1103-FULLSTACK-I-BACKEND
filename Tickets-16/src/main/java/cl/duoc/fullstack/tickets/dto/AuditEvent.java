package cl.duoc.fullstack.tickets.dto;

import java.time.LocalDateTime;

public record AuditEvent(
    Long id,
    String action,
    String entityType,
    Long entityId,
    Long userId,
    String username,
    String details,
    LocalDateTime timestamp
) {}
