package cl.duoc.fullstack.tickets.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cl.duoc.fullstack.tickets.dto.UserRequest;
import cl.duoc.fullstack.tickets.dto.UserResult;
import cl.duoc.fullstack.tickets.model.User;
import cl.duoc.fullstack.tickets.respository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository repository;

  @InjectMocks
  private UserService service;

  @Test
  void create_shouldSaveUser_whenEmailIsAvailable() {
    when(repository.existsByEmail("ana@duoc.cl")).thenReturn(false);
    when(repository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setId(1L);
      return user;
    });

    UserResult result = service.create(new UserRequest("Ana", "ana@duoc.cl"));

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.email()).isEqualTo("ana@duoc.cl");
  }

  @Test
  void create_shouldRejectUser_whenEmailAlreadyExists() {
    when(repository.existsByEmail("ana@duoc.cl")).thenReturn(true);

    assertThatThrownBy(() -> service.create(new UserRequest("Ana", "ana@duoc.cl")))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void getAll_shouldMapUsersToResults() {
    when(repository.findAll()).thenReturn(List.of(user(1L, "Ana", "ana@duoc.cl")));

    assertThat(service.getAll())
        .singleElement()
        .extracting(UserResult::email)
        .isEqualTo("ana@duoc.cl");
  }

  @Test
  void getById_shouldReturnEmpty_whenUserDoesNotExist() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThat(service.getById(99L)).isEmpty();
  }

  private User user(Long id, String name, String email) {
    return new User(id, name, email, User.Role.USER, true);
  }
}
