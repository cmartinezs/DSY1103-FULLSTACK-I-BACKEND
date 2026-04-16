package cl.duoc.fullstack.audit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private static final Logger logger = LoggerFactory.getLogger(AuditController.class);
    private final Map<Long, Map<String, Object>> auditLogs = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @PostMapping
    public Map<String, Object> logEvent(@RequestBody Map<String, String> request) {
        Long id = idCounter.getAndIncrement();
        Map<String, Object> event = Map.of(
            "id", id,
            "action", request.get("action"),
            "entityType", request.getOrDefault("entityType", "Ticket"),
            "entityId", Long.parseLong(request.getOrDefault("entityId", "0")),
            "userId", Long.parseLong(request.getOrDefault("userId", "0")),
            "username", request.getOrDefault("username", "system"),
            "details", request.getOrDefault("details", ""),
            "timestamp", System.currentTimeMillis()
        );
        auditLogs.put(id, new ConcurrentHashMap<>(event));
        logger.info("Audit: {} - {} #{} by {}", 
            request.get("action"), request.get("entityType"), request.get("entityId"), request.get("username"));
        return event;
    }

    @GetMapping
    public List<Map<String, Object>> listAuditLogs() {
        return new ArrayList<>(auditLogs.values());
    }

    @GetMapping("/ticket/{ticketId}")
    public List<Map<String, Object>> getAuditByTicket(@PathVariable Long ticketId) {
        return auditLogs.values().stream()
            .filter(log -> ticketId.equals(log.get("entityId")))
            .collect(Collectors.toList());
    }
}