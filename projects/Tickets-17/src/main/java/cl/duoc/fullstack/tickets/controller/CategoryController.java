package cl.duoc.fullstack.tickets.controller;

import cl.duoc.fullstack.tickets.dto.CategoryRequest;
import cl.duoc.fullstack.tickets.model.Category;
import cl.duoc.fullstack.tickets.model.ErrorResponse;
import cl.duoc.fullstack.tickets.service.CategoryService;
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
@RequestMapping("/ticket-app/categories")
public class CategoryController {
  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping
  public ResponseEntity<List<Category>> findAll() {
    return ResponseEntity.ok(categoryService.findAll());
  }

  @GetMapping("/by-id/{id}")
  public ResponseEntity<Category> findById(@PathVariable Long id) {
    return categoryService.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<String> create(@Valid @RequestBody CategoryRequest request) {
    categoryService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body("Category Created");
  }

  @PutMapping("/by-id/{id}")
  public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
    return categoryService.update(id, request)
        .map(category -> ResponseEntity.ok("Category Updated"))
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/by-id/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    return categoryService.deleteById(id)
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
