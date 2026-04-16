package cl.duoc.fullstack.tickets.controller;

import cl.duoc.fullstack.tickets.dto.TicketRequest;
import cl.duoc.fullstack.tickets.dto.TicketResult;
import cl.duoc.fullstack.tickets.model.ErrorResponse;
import cl.duoc.fullstack.tickets.service.TicketService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

  private final TicketService service;

  public TicketController(TicketService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<List<TicketResult>> getAllTickets(
      @RequestParam(required = false) String status) {
    List<TicketResult> tickets = status != null
        ? this.service.getTickets(status)
        : this.service.getTickets();
    return ResponseEntity.ok(tickets);
  }

  @PostMapping
  public ResponseEntity<Object> create(@Valid @RequestBody TicketRequest request) {
    try {
      TicketResult result = this.service.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(result);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }
  }

  @GetMapping("/by-id/{id}")
  public ResponseEntity<TicketResult> getTicketById(@PathVariable Long id) {
    return this.service.getById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/by-id/{id}")
  public ResponseEntity<Object> updateTicketById(
      @PathVariable Long id,
      @Valid @RequestBody TicketRequest request) {
    try {
      Optional<TicketResult> updated = this.service.updateById(id, request);
      if (updated.isPresent()) {
        return ResponseEntity.ok(updated.get());
      }
      return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }
  }

  @DeleteMapping("/by-id/{id}")
  public ResponseEntity<Void> deleteTicketById(@PathVariable Long id) {
    if (!this.service.deleteById(id)) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return ResponseEntity.badRequest().body(new ErrorResponse(message));
  }
}