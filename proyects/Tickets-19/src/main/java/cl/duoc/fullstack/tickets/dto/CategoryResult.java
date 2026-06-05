package cl.duoc.fullstack.tickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de categoria")
public record CategoryResult(
    @Schema(example = "1")
    Long id,
    @Schema(example = "Soporte tecnico")
    String name,
    @Schema(example = "Incidentes relacionados con soporte tecnico")
    String description
) {}
