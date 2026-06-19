package cl.duoc.fullstack.notifications.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class NotificationServiceTest {

    private final NotificationService service = new NotificationService();

    @Test
    void createNotification_shouldCreateNotificationWithDefaults() {
        Map<String, Object> result = service.createNotification(Map.of(
            "title", "Ticket creado",
            "message", "Ticket #1"
        ));

        assertThat(result)
            .containsEntry("id", 1L)
            .containsEntry("type", "INFO")
            .containsEntry("recipient", "all")
            .containsEntry("sent", false);
    }

    @Test
    void listNotifications_shouldReturnCreatedNotifications() {
        service.createNotification(Map.of("title", "A", "message", "Uno"));
        service.createNotification(Map.of("title", "B", "message", "Dos"));

        assertThat(service.listNotifications()).hasSize(2);
    }

    @Test
    void getNotification_shouldReturnNotification_whenItExists() {
        service.createNotification(Map.of("title", "A", "message", "Uno"));

        assertThat(service.getNotification(1L)).containsEntry("title", "A");
    }

    @Test
    void getNotification_shouldReturnError_whenItDoesNotExist() {
        assertThat(service.getNotification(99L))
            .containsEntry("error", "Notification not found")
            .containsEntry("id", 99L);
    }
}
