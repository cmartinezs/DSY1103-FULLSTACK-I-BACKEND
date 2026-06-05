package cl.duoc.fullstack.tickets.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
  private Long id;
  private String title;
  private String description;
  private String status;
  private LocalDateTime createdAt;
  private LocalDate estimatedResolutionDate;
  private LocalDateTime effectiveResolutionDate;
  private String createdBy;
  private String assignedTo;
}
