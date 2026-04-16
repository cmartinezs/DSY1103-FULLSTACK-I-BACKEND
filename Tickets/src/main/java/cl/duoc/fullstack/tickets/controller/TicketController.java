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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
public class TicketController {

  private TicketService service;

  public TicketController(TicketService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<List<Ticket>> getAllTickets(
      @RequestParam(required = false) String status) {
    List<Ticket> tickets = status != null 
        ? this.service.getTickets(status) 
        : this.service.getTickets();
    return ResponseEntity.ok(tickets);
  }

  @PostMapping
  public ResponseEntity<Object> create(@Valid @RequestBody Ticket ticket) {
    try {
      Ticket created = this.service.create(ticket);
      return ResponseEntity.status(HttpStatus.CREATED).body("Ticket Creado");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }

  @GetMapping("/by-id/{id}")
  public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
    Ticket found = this.service.getById(id);
    if (found != null) {
      return ResponseEntity.status(200).body(found);
    }
    return ResponseEntity.notFound().build();
  }

  @PutMapping("/by-id/{id}")
  public ResponseEntity<Object> updateTicketById(
      @PathVariable Long id,
      @Valid @RequestBody Ticket ticket) {
    try {
      Ticket updated = this.service.updateById(id, ticket);
      if (updated != null) {
        return ResponseEntity.status(200).body(updated);
      }
      return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
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

