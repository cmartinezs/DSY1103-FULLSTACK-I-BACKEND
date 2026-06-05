package cl.duoc.fullstack.tickets.client;

import cl.duoc.fullstack.tickets.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class NotificationClient {

  private final RestClient restClient;

  // Spring inyecta el RestClient.Builder preconfigurado (no lo instanciamos nosotros)
  public NotificationClient(RestClient.Builder builder) {
    this.restClient = builder
        .baseUrl("http://localhost:8081")  // URL base de NotificationService
        .build();                           // materializa el cliente inmutable para esta clase
  }

  // Patron fire-and-forget: si falla, la operacion principal ya se completo
  public void send(String title, String message, String type, String recipient) {
    try {
      NotificationRequest request = new NotificationRequest(title, message, type, recipient);

      restClient.post()
          .uri("/api/notifications")
          .body(request)
          .retrieve()
          .toBodilessEntity();

      log.info("Notificacion enviada a '{}': {}", recipient, title);
    } catch (Exception e) {
      // Si la notificacion falla, el ticket ya fue guardado: no revertimos nada.
      log.error("Error enviando notificacion a '{}': {}", recipient, e.getMessage());
    }
  }
}
