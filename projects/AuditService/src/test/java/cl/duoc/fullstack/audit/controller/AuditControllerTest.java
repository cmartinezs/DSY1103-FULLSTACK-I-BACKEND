package cl.duoc.fullstack.audit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.fullstack.audit.service.AuditService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    @Mock
    private AuditService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuditController(service)).build();
    }

    @Test
    void logEvent_shouldReturnCreatedEvent() throws Exception {
        when(service.logEvent(any())).thenReturn(Map.of("id", 1L, "action", "CREATE"));

        mockMvc.perform(post("/api/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"CREATE\",\"entityId\":\"1\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.action").value("CREATE"));
    }

    @Test
    void listAuditLogs_shouldReturnEvents() throws Exception {
        when(service.listAuditLogs()).thenReturn(List.of(Map.of("id", 1L)));

        mockMvc.perform(get("/api/audit"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAuditByTicket_shouldDelegateTicketFilter() throws Exception {
        when(service.getAuditByTicket(7L)).thenReturn(List.of(Map.of("entityId", 7L)));

        mockMvc.perform(get("/api/audit/ticket/7"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].entityId").value(7));

        verify(service).getAuditByTicket(7L);
    }
}
