package cl.duoc.fullstack.tickets.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.fullstack.tickets.model.Category;
import cl.duoc.fullstack.tickets.service.CategoryService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

  @Mock
  private CategoryService service;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new CategoryController(service)).build();
  }

  @Test
  void findAll_shouldReturnCategories() throws Exception {
    when(service.findAll()).thenReturn(List.of(category()));

    mockMvc.perform(get("/ticket-app/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Hardware"));
  }

  @Test
  void findById_shouldReturnNotFound_whenCategoryDoesNotExist() throws Exception {
    when(service.findById(99L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/ticket-app/categories/by-id/99"))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_shouldReturnCreated() throws Exception {
    when(service.create(any())).thenReturn(category());

    mockMvc.perform(post("/ticket-app/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Hardware\",\"description\":\"Equipos\"}"))
        .andExpect(status().isCreated())
        .andExpect(content().string("Category Created"));
  }

  @Test
  void create_shouldReturnConflict_whenNameExists() throws Exception {
    when(service.create(any())).thenThrow(new IllegalArgumentException("Duplicada"));

    mockMvc.perform(post("/ticket-app/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Hardware\"}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Duplicada"));
  }

  @Test
  void update_shouldReturnOk_whenCategoryExists() throws Exception {
    when(service.update(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(Optional.of(category()));

    mockMvc.perform(put("/ticket-app/categories/by-id/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Hardware\"}"))
        .andExpect(status().isOk());
  }

  @Test
  void delete_shouldReturnNoContent_whenCategoryExists() throws Exception {
    when(service.deleteById(1L)).thenReturn(true);

    mockMvc.perform(delete("/ticket-app/categories/by-id/1"))
        .andExpect(status().isNoContent());
  }

  private Category category() {
    Category category = new Category();
    category.setId(1L);
    category.setName("Hardware");
    return category;
  }
}
