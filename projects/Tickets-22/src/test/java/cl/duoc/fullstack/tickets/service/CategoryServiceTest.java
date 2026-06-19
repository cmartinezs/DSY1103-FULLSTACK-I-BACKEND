package cl.duoc.fullstack.tickets.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.fullstack.tickets.dto.CategoryRequest;
import cl.duoc.fullstack.tickets.model.Category;
import cl.duoc.fullstack.tickets.respository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository repository;

  @InjectMocks
  private CategoryService service;

  @Test
  void create_shouldSaveCategory_whenNameIsAvailable() {
    CategoryRequest request = new CategoryRequest("Hardware", "Equipos");
    when(repository.existsByNameIgnoreCase("Hardware")).thenReturn(false);
    when(repository.save(org.mockito.ArgumentMatchers.any(Category.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Category result = service.create(request);

    assertThat(result.getName()).isEqualTo("Hardware");
    verify(repository).save(org.mockito.ArgumentMatchers.any(Category.class));
  }

  @Test
  void create_shouldRejectCategory_whenNameAlreadyExists() {
    when(repository.existsByNameIgnoreCase("Hardware")).thenReturn(true);

    assertThatThrownBy(() -> service.create(new CategoryRequest("Hardware", "Equipos")))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void findAll_shouldReturnRepositoryCategories() {
    when(repository.findAll()).thenReturn(List.of(category(1L, "Hardware")));

    assertThat(service.findAll()).hasSize(1);
  }

  @Test
  void findById_shouldReturnCategory_whenItExists() {
    when(repository.findById(1L)).thenReturn(Optional.of(category(1L, "Hardware")));

    assertThat(service.findById(1L)).isPresent();
  }

  @Test
  void update_shouldModifyCategory_whenItExists() {
    when(repository.findById(1L)).thenReturn(Optional.of(category(1L, "Hardware")));
    when(repository.save(org.mockito.ArgumentMatchers.any(Category.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Category result = service.update(1L, new CategoryRequest("Software", "Aplicaciones")).orElseThrow();

    assertThat(result.getName()).isEqualTo("Software");
  }

  @Test
  void deleteById_shouldDeleteCategory_whenItExists() {
    when(repository.existsById(1L)).thenReturn(true);

    assertThat(service.deleteById(1L)).isTrue();
    verify(repository).deleteById(1L);
  }

  private Category category(Long id, String name) {
    Category category = new Category();
    category.setId(id);
    category.setName(name);
    return category;
  }
}
