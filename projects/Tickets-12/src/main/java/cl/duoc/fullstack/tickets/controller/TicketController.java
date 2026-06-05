package cl.duoc.fullstack.tickets.controller;

import cl.duoc.fullstack.tickets.dto.AssignTicketRequest;
import cl.duoc.fullstack.tickets.dto.TicketCommand;
import cl.duoc.fullstack.tickets.dto.TicketRequest;
import cl.duoc.fullstack.tickets.dto.TicketResponse;
import cl.duoc.fullstack.tickets.dto.TicketResult;
import cl.duoc.fullstack.tickets.exception.BadRequestException;
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
import org.springframework.web.bind.annotation.PatchMapping;
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
  public ResponseEntity<List<TicketResponse>> getAllTickets(
      @RequestParam(required = false) String status) {
    List<TicketResult> results = status != null
        ? this.service.getTickets(status)
        : this.service.getTickets();
    List<TicketResponse> responses = results.stream()
        .map(this::toResponse)
        .toList();
    return ResponseEntity.ok(responses);
  }

  @PostMapping
  public ResponseEntity<?> create(@Valid @RequestBody TicketRequest request) {
    try {
      TicketCommand command = toCommand(request);
      TicketResult result = this.service.create(command);
      TicketResponse response = toResponse(result);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    } catch (BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }
  }

  @GetMapping("/by-id/{id}")
  public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
    return this.service.getById(id)
        .map(result -> toResponse(result))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/by-id/{id}")
  public ResponseEntity<Object> updateTicketById(
      @PathVariable Long id,
      @Valid @RequestBody TicketRequest request) {
    try {
      TicketCommand command = toCommand(request);
      Optional<TicketResult> result = this.service.updateById(id, command);
      if (result.isPresent()) {
        return ResponseEntity.ok(toResponse(result.get()));
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

  @PatchMapping("/by-id/{id}")
  public ResponseEntity<?> assignTicket(
      @PathVariable Long id,
      @Valid @RequestBody AssignTicketRequest request) {
    try {
      Optional<TicketResult> result = this.service.assignTicket(id, request.getAssignedToEmail());
      if (result.isEmpty()) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(toResponse(result.get()));
    } catch (BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ErrorResponse(e.getMessage()));
    }
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return ResponseEntity.badRequest().body(new ErrorResponse(message));
  }

  private TicketCommand toCommand(TicketRequest request) {
    return new TicketCommand(
        request.title(),
        request.description(),
        request.status(),
        request.effectiveResolutionDate(),
        request.createdByEmail()
    );
  }

  private TicketResponse toResponse(TicketResult result) {
    return new TicketResponse(
        result.id(),
        result.title(),
        result.description(),
        result.status(),
        result.createdAt(),
        result.estimatedResolutionDate(),
        result.effectiveResolutionDate(),
        result.createdBy(),
        result.assignedTo()
    );
  }
}

