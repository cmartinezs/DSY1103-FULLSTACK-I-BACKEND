package cl.duoc.fullstack.tickets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  private String description;
  private String status;
  private LocalDateTime createdAt;
  private LocalDate estimatedResolutionDate;
  private LocalDateTime effectiveResolutionDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_id")
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private User createdBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_to_id")
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private User assignedTo;
}