package cl.duoc.fullstack.tickets.client;

import cl.duoc.fullstack.tickets.dto.AuditEvent;
import cl.duoc.fullstack.tickets.dto.AuditRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditServiceClientFallback implements AuditServiceClient {

  @Override
  public AuditEvent logEvent(AuditRequest request) {
    log.warn("AuditService no disponible, evento no registrado: {}", request.action());
    return null;  // el ticket ya fue guardado; solo se pierde el log de auditoria
  }

  @Override
  public List<AuditEvent> getAuditByTicket(Long ticketId) {
    log.warn("AuditService no disponible, sin historial de auditoria para ticket {}", ticketId);
    return List.of();  // lista vacia en lugar de lanzar excepcion
  }
}
