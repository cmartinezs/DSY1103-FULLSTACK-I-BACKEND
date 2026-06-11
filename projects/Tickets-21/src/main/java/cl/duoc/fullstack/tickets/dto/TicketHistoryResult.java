package cl.duoc.fullstack.tickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Evento historico de cambio de ticket")
public record TicketHistoryResult(
    @Schema(example = "1")
    Long id,
    @Schema(example = "OPEN")
    String previousStatus,
    @Schema(example = "IN_PROGRESS")
    String newStatus,
    @Schema(example = "2026-06-04T11:00:00")
    LocalDateTime changedAt,
    @Schema(example = "Ticket asignado a soporte")
    String comment
) {}
