package cl.duoc.fullstack.tickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para crear o actualizar una categoria")
public record CategoryRequest(
    @Schema(description = "Nombre de la categoria", example = "Soporte tecnico")
    @NotBlank(message = "Category name cannot be blank")
    String name,
    @Schema(description = "Descripcion de la categoria", example = "Incidentes relacionados con soporte tecnico")
    String description
) {}
