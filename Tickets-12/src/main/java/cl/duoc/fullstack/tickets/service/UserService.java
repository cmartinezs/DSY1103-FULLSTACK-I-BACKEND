package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.UserRequest;
import cl.duoc.fullstack.tickets.dto.UserResult;
import cl.duoc.fullstack.tickets.model.User;
import cl.duoc.fullstack.tickets.model.User.Role;
import cl.duoc.fullstack.tickets.respository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository repository;

  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  public List<UserResult> getUsers() {
    return this.repository.findAll().stream()
        .map(this::toResult)
        .toList();
  }

  public UserResult create(UserRequest request) {
    boolean exists = this.repository.findByEmail(request.email()).isPresent();
    if (exists) {
      throw new IllegalArgumentException("Ya existe un usuario con el email: " + request.email());
    }

    User user = new User();
    user.setName(request.name());
    user.setEmail(request.email());
    user.setRole(Role.valueOf(request.role() != null ? request.role() : "USER"));
    user.setActive(request.active() != null ? request.active() : true);
    User saved = this.repository.save(user);
    return toResult(saved);
  }

  public Optional<UserResult> getById(Long id) {
    return this.repository.findById(id).map(this::toResult);
  }

  public Optional<UserResult> updateById(Long id, UserRequest request) {
    Optional<User> found = this.repository.findById(id);
    if (found.isEmpty()) {
      return Optional.empty();
    }

    User toUpdate = found.get();
    toUpdate.setName(request.name());
    toUpdate.setEmail(request.email());
    if (request.role() != null) {
      toUpdate.setRole(Role.valueOf(request.role()));
    }
    if (request.active() != null) {
      toUpdate.setActive(request.active());
    }
    User saved = this.repository.save(toUpdate);
    return Optional.of(toResult(saved));
  }

  public boolean deleteById(Long id) {
    if (this.repository.existsById(id)) {
      this.repository.deleteById(id);
      return true;
    }
    return false;
  }

  private UserResult toResult(User user) {
    return new UserResult(
        user.getId(),
        user.getName(),
        user.getEmail()
    );
  }
}