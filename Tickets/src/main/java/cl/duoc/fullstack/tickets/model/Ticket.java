package cl.duoc.fullstack.tickets.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Ticket {
  private Long id;
  private String title;
  private String description;
  private String status;
}
