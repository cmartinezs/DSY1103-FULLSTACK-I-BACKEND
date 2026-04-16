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

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<UserResult> getAll() {
    return userRepository.findAll().stream()
        .map(this::toResult)
        .toList();
  }

  public UserResult create(UserRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException(
          "Ya existe un usuario con el email '" + request.email() + "'");
    }
    User user = new User();
    user.setName(request.name());
    user.setEmail(request.email());
    User saved = userRepository.save(user);
    return toResult(saved);
  }

  public Optional<UserResult> getById(Long id) {
    return userRepository.findById(id).map(this::toResult);
  }

  private UserResult toResult(User user) {
    return new UserResult(user.getId(), user.getName(), user.getEmail());
  }
}