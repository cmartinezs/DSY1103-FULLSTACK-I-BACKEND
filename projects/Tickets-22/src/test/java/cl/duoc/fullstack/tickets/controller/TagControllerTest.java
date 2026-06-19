package cl.duoc.fullstack.tickets.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.fullstack.tickets.model.Tag;
import cl.duoc.fullstack.tickets.service.TagService;
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
class TagControllerTest {

  @Mock
  private TagService service;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new TagController(service)).build();
  }

  @Test
  void findAll_shouldReturnTags() throws Exception {
    when(service.findAll()).thenReturn(List.of(tag()));

    mockMvc.perform(get("/ticket-app/tags"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Urgente"));
  }

  @Test
  void findById_shouldReturnTag_whenItExists() throws Exception {
    when(service.findById(1L)).thenReturn(Optional.of(tag()));

    mockMvc.perform(get("/ticket-app/tags/by-id/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.color").value("#ff0000"));
  }

  @Test
  void create_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
    mockMvc.perform(post("/ticket-app/tags")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"\",\"color\":\"#ff0000\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_shouldReturnNotFound_whenTagDoesNotExist() throws Exception {
    when(service.update(org.mockito.ArgumentMatchers.eq(99L), any())).thenReturn(Optional.empty());

    mockMvc.perform(put("/ticket-app/tags/by-id/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Urgente\",\"color\":\"#ff0000\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_shouldReturnNotFound_whenTagDoesNotExist() throws Exception {
    when(service.deleteById(99L)).thenReturn(false);

    mockMvc.perform(delete("/ticket-app/tags/by-id/99"))
        .andExpect(status().isNotFound());
  }

  private Tag tag() {
    Tag tag = new Tag();
    tag.setId(1L);
    tag.setName("Urgente");
    tag.setColor("#ff0000");
    return tag;
  }
}
