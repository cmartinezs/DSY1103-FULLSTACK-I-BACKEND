package cl.duoc.fullstack.tickets.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @NotBlank(message = "Category name cannot be blank")
    String name,
    String description
) {}
