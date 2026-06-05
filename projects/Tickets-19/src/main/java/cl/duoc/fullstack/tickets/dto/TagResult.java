package cl.duoc.fullstack.tickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de tag")
public record TagResult(
    @Schema(example = "1")
    Long id,
    @Schema(example = "Urgente")
    String name,
    @Schema(example = "#ff0000")
    String color
) {}
