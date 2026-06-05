package cl.duoc.fullstack.tickets.controller;

import cl.duoc.fullstack.tickets.dto.TagRequest;
import cl.duoc.fullstack.tickets.model.ErrorResponse;
import cl.duoc.fullstack.tickets.model.Tag;
import cl.duoc.fullstack.tickets.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket-app/tags")
@io.swagger.v3.oas.annotations.tags.Tag(
    name = "Tags",
    description = "Operaciones para gestionar tags de tickets")
public class TagController {
  private final TagService tagService;

  public TagController(TagService tagService) {
    this.tagService = tagService;
  }

  @GetMapping
  @Operation(summary = "Listar tags", description = "Obtiene todos los tags disponibles")
  public ResponseEntity<List<Tag>> findAll() {
    return ResponseEntity.ok(tagService.findAll());
  }

  @GetMapping("/by-id/{id}")
  @Operation(summary = "Buscar tag por id")
  public ResponseEntity<Tag> findById(
      @Parameter(description = "ID del tag", example = "1")
      @PathVariable Long id) {
    return tagService.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @Operation(summary = "Crear tag", description = "Registra un nuevo tag")
  public ResponseEntity<String> create(@Valid @RequestBody TagRequest request) {
    tagService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body("Tag Created");
  }

  @PutMapping("/by-id/{id}")
  @Operation(summary = "Actualizar tag por id")
  public ResponseEntity<String> update(
      @Parameter(description = "ID del tag", example = "1")
      @PathVariable Long id,
      @Valid @RequestBody TagRequest request) {
    return tagService.update(id, request)
        .map(tag -> ResponseEntity.ok("Tag Updated"))
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/by-id/{id}")
  @Operation(summary = "Eliminar tag por id")
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID del tag", example = "1")
      @PathVariable Long id) {
    return tagService.deleteById(id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleConflict(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(message));
  }
}
