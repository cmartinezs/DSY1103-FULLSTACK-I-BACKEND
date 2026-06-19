package cl.duoc.fullstack.tickets.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.fullstack.tickets.dto.TicketResult;
import cl.duoc.fullstack.tickets.service.TicketLinkAssembler;
import cl.duoc.fullstack.tickets.service.TicketService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

  @Mock
  private TicketService service;

  @Mock
  private TicketLinkAssembler assembler;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new TicketController(service, assembler)).build();
  }

  @Test
  void getAllTickets_shouldReturnTickets() throws Exception {
    TicketResult ticket = ticket(1L, "OPEN");
    when(service.getTickets()).thenReturn(List.of(ticket));
    when(assembler.toModel(ticket)).thenReturn(EntityModel.of(ticket));

    mockMvc.perform(get("/tickets"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1));
  }

  @Test
  void getAllTickets_shouldApplyStatusFilter() throws Exception {
    when(service.getTickets("OPEN")).thenReturn(List.of());

    mockMvc.perform(get("/tickets").param("status", "OPEN"))
        .andExpect(status().isOk());

    verify(service).getTickets("OPEN");
  }

  @Test
  void create_shouldReturnCreated_whenRequestIsValid() throws Exception {
    TicketResult ticket = ticket(1L, "NEW");
    when(service.create(any())).thenReturn(ticket);
    when(assembler.toModel(ticket)).thenReturn(EntityModel.of(ticket));

    mockMvc.perform(post("/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validRequestJson()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void create_shouldReturnConflict_whenBusinessRuleFails() throws Exception {
    when(service.create(any())).thenThrow(new IllegalArgumentException("Ticket duplicado"));

    mockMvc.perform(post("/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validRequestJson()))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Ticket duplicado"));
  }

  @Test
  void create_shouldReturnBadRequest_whenTitleIsBlank() throws Exception {
    mockMvc.perform(post("/tickets")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\"\",\"description\":\"Detalle\",\"createdByName\":\"ana@duoc.cl\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getTicketById_shouldReturnNotFound_whenTicketDoesNotExist() throws Exception {
    when(service.getById(99L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/tickets/by-id/99"))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateTicketById_shouldReturnUpdatedTicket() throws Exception {
    TicketResult ticket = ticket(1L, "OPEN");
    when(service.updateById(org.mockito.ArgumentMatchers.eq(1L), any()))
        .thenReturn(Optional.of(ticket));
    when(assembler.toModel(ticket)).thenReturn(EntityModel.of(ticket));

    mockMvc.perform(put("/tickets/by-id/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validRequestJson()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("OPEN"));
  }

  @Test
  void deleteTicketById_shouldReturnNoContent_whenTicketExists() throws Exception {
    when(service.deleteById(1L)).thenReturn(true);

    mockMvc.perform(delete("/tickets/by-id/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void getTicketHistory_shouldReturnHistory() throws Exception {
    when(service.getTicketHistory(1L)).thenReturn(List.of());

    mockMvc.perform(get("/tickets/1/history"))
        .andExpect(status().isOk());

    verify(service).getTicketHistory(1L);
  }

  private TicketResult ticket(Long id, String status) {
    return new TicketResult(id, "Problema", "Detalle", status, null, null, null, null, null, null, null);
  }

  private String validRequestJson() {
    return """
        {
          "title": "Problema",
          "description": "Detalle",
          "createdByName": "ana@duoc.cl",
          "status": "OPEN"
        }
        """;
  }
}
