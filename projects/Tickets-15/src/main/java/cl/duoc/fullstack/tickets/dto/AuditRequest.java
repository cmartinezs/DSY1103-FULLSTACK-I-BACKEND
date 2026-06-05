package cl.duoc.fullstack.tickets.dto;

public record AuditRequest(
    String action,
    String entityType,
    Long entityId,
    Long userId,
    String username,
    String details
) {}
