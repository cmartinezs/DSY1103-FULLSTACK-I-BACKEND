package cl.duoc.fullstack.tickets.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import cl.duoc.fullstack.tickets.dto.TicketHistoryResult;
import cl.duoc.fullstack.tickets.dto.TicketRequest;
import cl.duoc.fullstack.tickets.dto.TicketResult;
import cl.duoc.fullstack.tickets.model.ErrorResponse;
import cl.duoc.fullstack.tickets.service.TicketLinkAssembler;
import cl.duoc.fullstack.tickets.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
@Tag(name = "Tickets", description = "Operaciones para gestionar tickets de soporte")
public class TicketController {

  private final TicketService service;
  private final TicketLinkAssembler assembler;

  public TicketController(TicketService service, TicketLinkAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }

  @GetMapping
  @Operation(
      summary = "Listar tickets",
      description = "Obtiene todos los tickets. Cada item incluye enlaces HATEOAS en _links. Permite filtrar por estado usando el parametro opcional status.")
  @ApiResponse(responseCode = "200", description = "Tickets obtenidos correctamente")
  public ResponseEntity<CollectionModel<EntityModel<TicketResult>>> getAllTickets(
      @Parameter(description = "Estado del ticket para filtrar resultados", example = "OPEN")
      @RequestParam(required = false) String status) {
    List<TicketResult> tickets = status != null
        ? this.service.getTickets(status)
        : this.service.getTickets();

    List<EntityModel<TicketResult>> models = tickets.stream()
        .map(assembler::toModel)
        .toList();

    CollectionModel<EntityModel<TicketResult>> collection = CollectionModel.of(models);
    collection.add(linkTo(methodOn(TicketController.class).getAllTickets(null)).withSelfRel());

    return ResponseEntity.ok(collection);
  }

  @PostMapping
  @Operation(summary = "Crear ticket", description = "Registra un nuevo ticket de soporte")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Ticket creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "Conflicto de negocio", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Object> create(@Valid @RequestBody TicketRequest request) {
    try {
      TicketResult result = this.service.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(result));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }
  }

  @GetMapping("/by-id/{id}")
  @Operation(
      summary = "Buscar ticket por id",
      description = "Devuelve el ticket con enlaces HATEOAS en _links. Incluye self, all, update (si OPEN) y delete (si OPEN).")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
      @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
  })
  public ResponseEntity<EntityModel<TicketResult>> getTicketById(
      @Parameter(description = "ID del ticket", example = "1")
      @PathVariable Long id) {
    return this.service.getById(id)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/by-id/{id}")
  @Operation(summary = "Actualizar ticket por id", description = "Actualiza los datos de un ticket existente")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Ticket actualizado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
      @ApiResponse(responseCode = "409", description = "Conflicto de negocio", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Object> updateTicketById(
      @Parameter(description = "ID del ticket", example = "1") @PathVariable Long id,
      @Valid @RequestBody TicketRequest request) {
    try {
      Optional<TicketResult> updated = this.service.updateById(id, request);
      if (updated.isPresent()) {
        return ResponseEntity.ok(assembler.toModel(updated.get()));
      }
      return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }
  }

  @DeleteMapping("/by-id/{id}")
  @Operation(summary = "Eliminar ticket por id", description = "Elimina un ticket existente")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Ticket eliminado correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
  })
  public ResponseEntity<Void> deleteTicketById(
      @Parameter(description = "ID del ticket", example = "1")
      @PathVariable Long id) {
    if (!this.service.deleteById(id)) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/history")
  @Operation(summary = "Ver historial del ticket", description = "Obtiene los eventos historicos asociados a un ticket")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado")
  })
  public ResponseEntity<List<TicketHistoryResult>> getTicketHistory(
      @Parameter(description = "ID del ticket", example = "1")
      @PathVariable Long id) {
    List<TicketHistoryResult> history = this.service.getTicketHistory(id);
    return ResponseEntity.ok(history);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return ResponseEntity.badRequest().body(new ErrorResponse(message));
  }
}
