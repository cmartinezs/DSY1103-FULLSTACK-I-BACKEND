package cl.duoc.fullstack.sla.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.fullstack.sla.service.SlaService;
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
class SlaControllerTest {

    @Mock
    private SlaService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SlaController(service)).build();
    }

    @Test
    void startSla_shouldReturnRecord() throws Exception {
        when(service.startSla(any())).thenReturn(Map.of("ticketId", 1L, "status", "OPEN"));

        mockMvc.perform(post("/api/sla/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ticketId\":\"1\",\"priority\":\"HIGH\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void getSla_shouldReturnNotFound_whenItDoesNotExist() throws Exception {
        when(service.getSla(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/sla/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void closeSla_shouldReturnClosedRecord() throws Exception {
        when(service.closeSla(1L)).thenReturn(Optional.of(Map.of("ticketId", 1L, "status", "CLOSED")));

        mockMvc.perform(put("/api/sla/1/close"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CLOSED"));

        verify(service).closeSla(1L);
    }

    @Test
    void listAll_shouldReturnRecords() throws Exception {
        when(service.listAll()).thenReturn(List.of(Map.of("ticketId", 1L)));

        mockMvc.perform(get("/api/sla"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].ticketId").value(1));
    }
}
