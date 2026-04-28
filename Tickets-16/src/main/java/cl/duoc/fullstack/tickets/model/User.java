package cl.duoc.fullstack.tickets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El nombre es requerido")
  @Column(nullable = false, length = 100)
  private String name;

  @NotBlank(message = "El email es requerido")
  @Email(message = "El email no tiene un formato válido")
  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Column(length = 255)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role = Role.USER;

  @Column(nullable = false)
  private boolean active = true;

  @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
  private List<Ticket> createdTickets = new ArrayList<>();

  @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
  private List<Ticket> assignedTickets = new ArrayList<>();

  public enum Role {
    USER,
    AGENT,
    ADMIN
  }
}
