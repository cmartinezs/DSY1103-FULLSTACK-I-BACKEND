package cl.duoc.fullstack.tickets.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
    @NotBlank(message = "El nombre es requerido")
    String name,
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email no tiene un formato válido")
    String email
) {}