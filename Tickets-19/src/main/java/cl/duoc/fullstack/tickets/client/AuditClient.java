package cl.duoc.fullstack.tickets.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "auditService",
    url = "${audit.service.url:http://localhost:8082}"
)
public interface AuditClient {

    @PostMapping("/api/audit")
    Map<String, Object> logEvent(@RequestBody Map<String, String> event);

    @GetMapping("/api/audit")
    List<Map<String, Object>> listEvents();

    @GetMapping("/api/audit/ticket/{ticketId}")
    List<Map<String, Object>> getEventsByTicket(@PathVariable("ticketId") Long ticketId);
}