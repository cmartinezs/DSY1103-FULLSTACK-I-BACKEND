package cl.duoc.fullstack.audit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final Map<Long, Map<String, Object>> auditLogs = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Map<String, Object> logEvent(Map<String, String> request) {
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

    public List<Map<String, Object>> listAuditLogs() {
        return new ArrayList<>(auditLogs.values());
    }

    public List<Map<String, Object>> getAuditByTicket(Long ticketId) {
        return auditLogs.values().stream()
            .filter(log -> ticketId.equals(log.get("entityId")))
            .toList();
    }
}
