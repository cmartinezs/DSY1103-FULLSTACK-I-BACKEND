package cl.duoc.fullstack.tickets.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.fullstack.tickets.dto.TagRequest;
import cl.duoc.fullstack.tickets.model.Tag;
import cl.duoc.fullstack.tickets.respository.TagRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

  @Mock
  private TagRepository repository;

  @InjectMocks
  private TagService service;

  @Test
  void create_shouldSaveTag_whenNameIsAvailable() {
    when(repository.existsByNameIgnoreCase("Urgente")).thenReturn(false);
    when(repository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Tag result = service.create(new TagRequest("Urgente", "#ff0000"));

    assertThat(result.getColor()).isEqualTo("#ff0000");
  }

  @Test
  void create_shouldRejectTag_whenNameAlreadyExists() {
    when(repository.existsByNameIgnoreCase("Urgente")).thenReturn(true);

    assertThatThrownBy(() -> service.create(new TagRequest("Urgente", "#ff0000")))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void findAll_shouldReturnRepositoryTags() {
    when(repository.findAll()).thenReturn(List.of(tag(1L, "Urgente")));

    assertThat(service.findAll()).hasSize(1);
  }

  @Test
  void findById_shouldReturnEmpty_whenTagDoesNotExist() {
    when(repository.findById(99L)).thenReturn(Optional.empty());

    assertThat(service.findById(99L)).isEmpty();
  }

  @Test
  void update_shouldModifyTag_whenItExists() {
    when(repository.findById(1L)).thenReturn(Optional.of(tag(1L, "Urgente")));
    when(repository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Tag result = service.update(1L, new TagRequest("Critico", "#000000")).orElseThrow();

    assertThat(result.getName()).isEqualTo("Critico");
  }

  @Test
  void deleteById_shouldReturnFalse_whenTagDoesNotExist() {
    when(repository.existsById(99L)).thenReturn(false);

    assertThat(service.deleteById(99L)).isFalse();
    verify(repository).existsById(99L);
  }

  private Tag tag(Long id, String name) {
    Tag tag = new Tag();
    tag.setId(id);
    tag.setName(name);
    return tag;
  }
}
