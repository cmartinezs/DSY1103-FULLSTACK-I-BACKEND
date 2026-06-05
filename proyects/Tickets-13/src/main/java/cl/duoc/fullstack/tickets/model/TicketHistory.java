package cl.duoc.fullstack.tickets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticket_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ticket_id", nullable = false)
  private Ticket ticket;

  @Column(name = "previous_status", length = 20)
  private String previousStatus;

  @Column(name = "new_status", length = 20)
  private String newStatus;

  @Column(name = "previous_assigned_email", length = 150)
  private String previousAssignedEmail;

  @Column(name = "new_assigned_email", length = 150)
  private String newAssignedEmail;

  @Column(name = "changed_at", nullable = false)
  private LocalDateTime changedAt;

  @Column(length = 255)
  private String comment;
}
