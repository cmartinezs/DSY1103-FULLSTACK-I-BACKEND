package cl.duoc.fullstack.tickets.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

  @NotBlank(message = "El nombre es requerido")
  private String name;

  @NotBlank(message = "El email es requerido")
  @Email(message = "El email no tiene un formato válido")
  private String email;
}
