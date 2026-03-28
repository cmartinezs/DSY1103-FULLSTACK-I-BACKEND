package cl.duoc.fullstack.tickets.controller;

import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.service.TicketService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  @PostMapping
  public ResponseEntity<String> create(@Valid @RequestBody Ticket ticket) {
    Ticket created = this.service.create(ticket);
    if (created != null) {
      return ResponseEntity.status(HttpStatus.CREATED).body("Ticket Creado");
    }
    return ResponseEntity.badRequest().build();
  }

  @PutMapping("/by-id/{id}")
  public ResponseEntity<Ticket> getTicketById(
      @PathVariable Long id,
      @RequestBody Ticket ticket) {
    Ticket updated = this.service.updateById(id, ticket);
    if (updated != null) {
      return ResponseEntity.status(200).body(updated);
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/by-id/{id}")
  public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
    Ticket found = this.service.getById(id);
    if (found != null) {
      return ResponseEntity.status(200).body(found);
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("/by-id/{id}")
  public ResponseEntity<Ticket> deleteTicketById(@PathVariable Long id) {
    Ticket found = this.service.deleteById(id);
    if (found != null) {
      return ResponseEntity.status(200).body(found);
    }
    return ResponseEntity.notFound().build();
  }
}
