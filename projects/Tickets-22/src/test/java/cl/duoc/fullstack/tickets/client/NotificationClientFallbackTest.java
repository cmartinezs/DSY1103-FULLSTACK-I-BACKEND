package cl.duoc.fullstack.tickets.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class NotificationClientFallbackTest {

  private final NotificationClientFallback fallback = new NotificationClientFallback();

  @Test
  void createNotification_shouldReturnSafeResponse_whenServiceIsUnavailable() {
    // given
    Map<String, String> notification = Map.of("title", "Ticket creado");

    // when
    Map<String, Object> result = fallback.createNotification(notification);

    // then
    assertThat(result).containsEntry("status", "fallback");
  }

  @Test
  void listNotifications_shouldReturnEmptyList_whenServiceIsUnavailable() {
    // given / when
    var result = fallback.listNotifications();

    // then
    assertThat(result).isEmpty();
  }

  @Test
  void getNotification_shouldReturnErrorResponse_whenServiceIsUnavailable() {
    // given / when
    Map<String, Object> result = fallback.getNotification(7L);

    // then
    assertThat(result)
        .containsEntry("error", "Notification service unavailable")
        .containsEntry("id", 7L);
  }
}
