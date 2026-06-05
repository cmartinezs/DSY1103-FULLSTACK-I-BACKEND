package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.UserRequest;
import cl.duoc.fullstack.tickets.dto.UserResult;
import cl.duoc.fullstack.tickets.model.User;
import cl.duoc.fullstack.tickets.respository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private UserRepository repository;

  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  public List<UserResult> getAll() {
    return repository.findAll().stream()
        .map(this::toResult)
        .toList();
  }

  public UserResult create(UserRequest request) {
    if (repository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException(
          "Ya existe un usuario con el email '" + request.getEmail() + "'");
    }
    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    return toResult(repository.save(user));
  }

  public Optional<UserResult> getById(Long id) {
    return repository.findById(id).map(this::toResult);
  }

  private UserResult toResult(User user) {
    return new UserResult(user.getId(), user.getName(), user.getEmail());
  }
}
