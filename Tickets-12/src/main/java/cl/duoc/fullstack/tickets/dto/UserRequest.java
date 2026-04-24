package cl.duoc.fullstack.tickets.dto;

public record UserRequest(
    String name,
    String email,
    String role,
    Boolean active
) {}