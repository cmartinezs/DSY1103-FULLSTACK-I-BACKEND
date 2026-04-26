package cl.duoc.fullstack.tickets.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignTicketRequest {

  @Email(message = "El email no tiene un formato valido")
  private String assignedToEmail;
}
