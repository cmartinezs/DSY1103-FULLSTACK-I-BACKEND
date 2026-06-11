package cl.duoc.fullstack.tickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de usuario")
public record UserResult(
    @Schema(example = "1")
    Long id,
    @Schema(example = "Ana Garcia")
    String name,
    @Schema(example = "ana.garcia@empresa.com")
    String email
) {}
