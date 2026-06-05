package cl.duoc.fullstack.tickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Respuesta con los datos completos del ticket")
public record TicketResult(
    @Schema(example = "1")
    Long id,
    @Schema(example = "No puedo ingresar al sistema")
    String title,
    @Schema(example = "El login rechaza mis credenciales")
    String description,
    @Schema(example = "OPEN")
    String status,
    @Schema(example = "2026-06-04T09:15:00")
    LocalDateTime createdAt,
    @Schema(example = "2026-06-06")
    LocalDate estimatedResolutionDate,
    @Schema(example = "2026-06-04T10:30:00")
    LocalDateTime effectiveResolutionDate,
    UserResult createdBy,
    UserResult assignedTo,
    CategoryResult category,
    List<TagResult> tags
) {}
