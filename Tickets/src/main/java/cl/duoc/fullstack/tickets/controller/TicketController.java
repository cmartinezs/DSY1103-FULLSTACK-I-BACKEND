package cl.duoc.fullstack.tickets.controller;

import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.service.TicketService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
public class TicketController {
  private TicketService service;
  public TicketController(TicketService service) {
    this.service = service;
  }

  @GetMapping
  public List<Ticket> getAllTickets() {
    return this.service.getTickets();
  }
}
