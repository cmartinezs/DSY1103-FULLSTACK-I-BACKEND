package cl.duoc.fullstack.tickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para crear un usuario")
public record UserRequest(
    @Schema(description = "Nombre completo del usuario", example = "Ana Garcia")
    @NotBlank(message = "El nombre es requerido")
    String name,
    @Schema(description = "Correo electronico del usuario", example = "ana.garcia@empresa.com")
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email no tiene un formato válido")
    String email
) {}
