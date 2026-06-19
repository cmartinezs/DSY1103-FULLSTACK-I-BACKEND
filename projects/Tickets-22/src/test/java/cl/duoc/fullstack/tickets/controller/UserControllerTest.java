package cl.duoc.fullstack.tickets.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.fullstack.tickets.dto.UserResult;
import cl.duoc.fullstack.tickets.service.UserService;
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
class UserControllerTest {

  @Mock
  private UserService service;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new UserController(service)).build();
  }

  @Test
  void getAll_shouldReturnUsers() throws Exception {
    when(service.getAll()).thenReturn(List.of(user()));

    mockMvc.perform(get("/ticket-app/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].email").value("ana@duoc.cl"));
  }

  @Test
  void create_shouldReturnCreated_whenRequestIsValid() throws Exception {
    when(service.create(any())).thenReturn(user());

    mockMvc.perform(post("/ticket-app/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Ana\",\"email\":\"ana@duoc.cl\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void create_shouldReturnConflict_whenEmailExists() throws Exception {
    when(service.create(any())).thenThrow(new IllegalArgumentException("Email duplicado"));

    mockMvc.perform(post("/ticket-app/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Ana\",\"email\":\"ana@duoc.cl\"}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Email duplicado"));
  }

  @Test
  void create_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
    mockMvc.perform(post("/ticket-app/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Ana\",\"email\":\"correo-invalido\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
    when(service.getById(99L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/ticket-app/users/by-id/99"))
        .andExpect(status().isNotFound());
  }

  private UserResult user() {
    return new UserResult(1L, "Ana", "ana@duoc.cl");
  }
}
