package cl.duoc.fullstack.tickets.client;

import cl.duoc.fullstack.tickets.dto.AuditEvent;
import cl.duoc.fullstack.tickets.dto.AuditRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "audit-service",                     // clave para configuracion en application.yml
    url = "http://localhost:8082",              // URL base de AuditService
    fallback = AuditServiceClientFallback.class
)
public interface AuditServiceClient {

  @PostMapping("/api/audit")
  AuditEvent logEvent(@RequestBody AuditRequest request);

  @GetMapping("/api/audit/ticket/{ticketId}")
  List<AuditEvent> getAuditByTicket(@PathVariable Long ticketId);
}
