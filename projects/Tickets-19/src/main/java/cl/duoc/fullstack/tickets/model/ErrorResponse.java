package cl.duoc.fullstack.tickets.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta uniforme de error")
public record ErrorResponse(
    @Schema(description = "Detalle del error", example = "Ticket no encontrado")
    String message
) {}
