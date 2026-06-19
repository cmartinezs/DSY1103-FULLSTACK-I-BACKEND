package cl.duoc.fullstack.audit.controller;

import cl.duoc.fullstack.audit.service.AuditService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping
    public Map<String, Object> logEvent(@RequestBody Map<String, String> request) {
        return auditService.logEvent(request);
    }

    @GetMapping
    public List<Map<String, Object>> listAuditLogs() {
        return auditService.listAuditLogs();
    }

    @GetMapping("/ticket/{ticketId}")
    public List<Map<String, Object>> getAuditByTicket(@PathVariable Long ticketId) {
        return auditService.getAuditByTicket(ticketId);
    }
}
