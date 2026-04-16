package cl.duoc.fullstack.tickets.client;

import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuditRestClient {

  private final RestClient restClient;

  public AuditRestClient() {
    this.restClient = RestClient.builder()
        .baseUrl("http://localhost:8082")
        .build();
  }

  public Map<String, Object> logEvent(Map<String, String> event) {
    try {
      return restClient.post()
          .uri("/api/audit")
          .body(event)
          .retrieve()
          .body(Map.class);
    } catch (Exception e) {
      return Map.of("status", "fallback", "message", "Audit service unavailable");
    }
  }

  public List<Map<String, Object>> listEvents() {
    try {
      return restClient.get()
          .uri("/api/audit")
          .retrieve()
          .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    } catch (Exception e) {
      return List.of();
    }
  }

  public List<Map<String, Object>> getEventsByTicket(Long ticketId) {
    try {
      return restClient.get()
          .uri("/api/audit/ticket/" + ticketId)
          .retrieve()
          .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    } catch (Exception e) {
      return List.of();
    }
  }
}