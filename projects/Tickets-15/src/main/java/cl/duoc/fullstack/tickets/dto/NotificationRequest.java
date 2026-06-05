package cl.duoc.fullstack.tickets.dto;

public record NotificationRequest(
    String title,
    String message,
    String type,
    String recipient
) {}
