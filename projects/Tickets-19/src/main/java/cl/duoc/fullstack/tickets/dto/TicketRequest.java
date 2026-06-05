package cl.duoc.fullstack.tickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Datos necesarios para crear o actualizar un ticket")
public record TicketRequest(
    @Schema(description = "Titulo corto del problema", example = "No puedo ingresar al sistema")
    @NotBlank(message = "El titulo es requerido")
    @Size(min = 1, max = 50)
    String title,
    @Schema(description = "Descripcion detallada del problema", example = "El login rechaza mis credenciales")
    @NotBlank(message = "La descripción es requerida")
    String description,
    @Schema(description = "Nombre del usuario que crea el ticket", example = "Ana Garcia")
    String createdByName,
    @Schema(description = "ID del usuario asignado", example = "2")
    Long assignedToId,
    @Schema(description = "ID de la categoria", example = "1")
    Long categoryId,
    @Schema(description = "IDs de tags asociados", example = "[1, 3]")
    List<Long> tagIds,
    @Schema(description = "Estado del ticket", example = "OPEN")
    String status,
    @Schema(description = "Fecha real de resolucion", example = "2026-06-04T10:30:00")
    LocalDateTime effectiveResolutionDate
) {}
