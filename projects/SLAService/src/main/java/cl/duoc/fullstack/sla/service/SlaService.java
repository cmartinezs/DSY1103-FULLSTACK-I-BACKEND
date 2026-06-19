package cl.duoc.fullstack.sla.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SlaService {

    private static final Logger logger = LoggerFactory.getLogger(SlaService.class);
    private static final Map<String, Long> DEADLINE_HOURS = Map.of(
        "HIGH", 24L,
        "MEDIUM", 72L,
        "LOW", 168L
    );

    private final Map<Long, Map<String, Object>> slaRecords = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Map<String, Object> startSla(Map<String, String> request) {
        Long ticketId = Long.parseLong(request.get("ticketId"));
        String priority = request.getOrDefault("priority", "MEDIUM").toUpperCase();
        Optional<Map<String, Object>> existing = getSla(ticketId);
        if (existing.isPresent() && "OPEN".equals(existing.get().get("status"))) {
            logger.warn("SLA already open for ticket #{}", ticketId);
            return existing.get();
        }

        long hours = DEADLINE_HOURS.getOrDefault(priority, 72L);
        Instant deadline = Instant.now().plus(hours, ChronoUnit.HOURS);
        Long id = idCounter.getAndIncrement();
        Map<String, Object> record = new ConcurrentHashMap<>();
        record.put("id", id);
        record.put("ticketId", ticketId);
        record.put("priority", priority);
        record.put("deadline", deadline.toString());
        record.put("status", "OPEN");
        record.put("startedAt", Instant.now().toString());
        slaRecords.put(id, record);
        logger.info("SLA started for ticket #{} — priority: {}, deadline: {}", ticketId, priority, deadline);
        return record;
    }

    public Optional<Map<String, Object>> getSla(Long ticketId) {
        return slaRecords.values().stream()
            .filter(record -> ticketId.equals(record.get("ticketId")))
            .findFirst();
    }

    public Optional<Map<String, Object>> closeSla(Long ticketId) {
        return getSla(ticketId).map(record -> {
            record.put("status", "CLOSED");
            record.put("closedAt", Instant.now().toString());
            logger.info("SLA closed for ticket #{}", ticketId);
            return record;
        });
    }

    public List<Map<String, Object>> listAll() {
        return new ArrayList<>(slaRecords.values());
    }
}
