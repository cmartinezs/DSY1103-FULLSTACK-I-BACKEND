package cl.duoc.fullstack.search.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.fullstack.search.service.SearchService;
import java.util.List;
import java.util.Map;
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
class SearchControllerTest {

    @Mock
    private SearchService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SearchController(service)).build();
    }

    @Test
    void indexTicket_shouldReturnNoContent() throws Exception {
        mockMvc.perform(post("/api/search/index")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ticketId\":\"1\",\"title\":\"Error\"}"))
            .andExpect(status().isNoContent());

        verify(service).indexTicket(any());
    }

    @Test
    void search_shouldReturnMatches() throws Exception {
        when(service.search("login")).thenReturn(List.of(Map.of("ticketId", 1L)));

        mockMvc.perform(get("/api/search").param("q", "login"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].ticketId").value(1));
    }

    @Test
    void getByTicket_shouldReturnEntry_whenItExists() throws Exception {
        when(service.getByTicket(7L)).thenReturn(Optional.of(Map.of("ticketId", 7L)));

        mockMvc.perform(get("/api/search/ticket/7"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ticketId").value(7));
    }

    @Test
    void getByTicket_shouldReturnNotFound_whenItDoesNotExist() throws Exception {
        when(service.getByTicket(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/search/ticket/99"))
            .andExpect(status().isNotFound());
    }
}
