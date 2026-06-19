package cl.duoc.fullstack.notifications.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.fullstack.notifications.service.NotificationService;
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
class NotificationControllerTest {

    @Mock
    private NotificationService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new NotificationController(service)).build();
    }

    @Test
    void createNotification_shouldReturnNotification() throws Exception {
        when(service.createNotification(any())).thenReturn(Map.of("id", 1L, "title", "Ticket creado"));

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Ticket creado\",\"message\":\"Ticket #1\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void listNotifications_shouldReturnNotifications() throws Exception {
        when(service.listNotifications()).thenReturn(List.of(Map.of("id", 1L)));

        mockMvc.perform(get("/api/notifications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getNotification_shouldDelegateLookup() throws Exception {
        when(service.getNotification(5L)).thenReturn(Map.of("id", 5L));

        mockMvc.perform(get("/api/notifications/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5));

        verify(service).getNotification(5L);
    }
}
