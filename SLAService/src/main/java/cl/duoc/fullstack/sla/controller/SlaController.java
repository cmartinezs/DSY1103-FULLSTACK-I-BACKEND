package cl.duoc.fullstack.sla.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sla")
public class SlaController {

    private static final Logger logger = LoggerFactory.getLogger(SlaController.class);
    private final Map<Long, Map<String, Object>> slaRecords = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /** Horas de plazo según prioridad */
    private static final Map<String, Long> DEADLINE_HOURS = Map.of(
            "HIGH",   24L,
            "MEDIUM", 72L,
            "LOW",    168L
    );

    /**
     * Inicia el SLA de un ticket.
     * Body: { ticketId, priority }  (priority: HIGH | MEDIUM | LOW)
     * Response: { id, ticketId, priority, deadline, status, startedAt }
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startSla(@RequestBody Map<String, String> request) {
        Long ticketId = Long.parseLong(request.get("ticketId"));
        String priority = request.getOrDefault("priority", "MEDIUM").toUpperCase();

        // Si ya existe un SLA abierto para este ticket, lo retorna sin crear uno nuevo
        Map<String, Object> existing = findByTicketId(ticketId);
        if (existing != null && "OPEN".equals(existing.get("status"))) {
            logger.warn("SLA already open for ticket #{}", ticketId);
            return ResponseEntity.ok(existing);
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

        return ResponseEntity.ok(record);
    }

    /**
     * Obtiene el SLA activo de un ticket.
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<Map<String, Object>> getSla(@PathVariable Long ticketId) {
        Map<String, Object> record = findByTicketId(ticketId);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }

    /**
     * Cierra el SLA de un ticket (al resolver o cerrar el ticket).
     */
    @PutMapping("/{ticketId}/close")
    public ResponseEntity<Map<String, Object>> closeSla(@PathVariable Long ticketId) {
        Map<String, Object> record = findByTicketId(ticketId);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        record.put("status", "CLOSED");
        record.put("closedAt", Instant.now().toString());
        logger.info("SLA closed for ticket #{}", ticketId);
        return ResponseEntity.ok(record);
    }

    /**
     * Lista todos los registros SLA.
     */
    @GetMapping
    public List<Map<String, Object>> listAll() {
        return new ArrayList<>(slaRecords.values());
    }

    private Map<String, Object> findByTicketId(Long ticketId) {
        return slaRecords.values().stream()
                .filter(r -> ticketId.equals(r.get("ticketId")))
                .findFirst()
                .orElse(null);
    }
}
