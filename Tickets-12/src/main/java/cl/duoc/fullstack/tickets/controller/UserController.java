package cl.duoc.fullstack.tickets.controller;

import cl.duoc.fullstack.tickets.dto.UserRequest;
import cl.duoc.fullstack.tickets.dto.UserResult;
import cl.duoc.fullstack.tickets.model.ErrorResponse;
import cl.duoc.fullstack.tickets.service.UserService;
import java.util.Optional;
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
@RequestMapping("/users")
public class UserController {

  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<Object> getAllUsers() {
    return ResponseEntity.ok(this.service.getUsers());
  }

  @PostMapping
  public ResponseEntity<Object> create(@RequestBody UserRequest request) {
    try {
      UserResult result = this.service.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(result);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }
  }

  @GetMapping("/by-id/{id}")
  public ResponseEntity<UserResult> getUserById(@PathVariable Long id) {
    return this.service.getById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/by-id/{id}")
  public ResponseEntity<Object> updateUserById(
      @PathVariable Long id,
      @RequestBody UserRequest request) {
    try {
      Optional<UserResult> updated = this.service.updateById(id, request);
      if (updated.isPresent()) {
        return ResponseEntity.ok(updated.get());
      }
      return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }
  }

  @DeleteMapping("/by-id/{id}")
  public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
    if (!this.service.deleteById(id)) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
  }
}