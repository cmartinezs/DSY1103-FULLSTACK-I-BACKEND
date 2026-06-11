package cl.duoc.fullstack.tickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para crear o actualizar un tag")
public record TagRequest(
    @Schema(description = "Nombre del tag", example = "Urgente")
    @NotBlank(message = "Tag name cannot be blank")
    String name,
    @Schema(description = "Color visual del tag", example = "#ff0000")
    String color
) {}
