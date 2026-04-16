package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.UserRequest;
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

  public List<User> getAll() {
    return userRepository.findAll();
  }

  public User create(UserRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException(
          "Ya existe un usuario con el email '" + request.email() + "'");
    }
    User user = new User();
    user.setName(request.name());
    user.setEmail(request.email());
    return userRepository.save(user);
  }

  public Optional<User> getById(Long id) {
    return userRepository.findById(id);
  }
}