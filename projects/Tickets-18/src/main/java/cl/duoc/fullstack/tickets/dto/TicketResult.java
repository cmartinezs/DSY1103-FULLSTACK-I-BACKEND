package cl.duoc.fullstack.tickets.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TicketResult(
    Long id,
    String title,
    String description,
    String status,
    LocalDateTime createdAt,
    LocalDate estimatedResolutionDate,
    LocalDateTime effectiveResolutionDate,
    UserResult createdBy,
    UserResult assignedTo,
    CategoryResult category,
    List<TagResult> tags
) {}