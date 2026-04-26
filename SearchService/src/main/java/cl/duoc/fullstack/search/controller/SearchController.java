package cl.duoc.fullstack.search.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    private final Map<Long, Map<String, Object>> index = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Indexa o reindexar un ticket.
     * Body: { ticketId, title, description, status }
     * Response: 204 No Content
     */
    @PostMapping("/index")
    public ResponseEntity<Void> indexTicket(@RequestBody Map<String, String> request) {
        Long ticketId = Long.parseLong(request.get("ticketId"));

        // Si ya existe una entrada para este ticket, la reemplaza
        Long existingId = index.values().stream()
                .filter(e -> ticketId.equals(e.get("ticketId")))
                .map(e -> (Long) e.get("id"))
                .findFirst()
                .orElse(null);

        Long id = existingId != null ? existingId : idCounter.getAndIncrement();

        Map<String, Object> entry = new ConcurrentHashMap<>();
        entry.put("id", id);
        entry.put("ticketId", ticketId);
        entry.put("title", request.getOrDefault("title", ""));
        entry.put("description", request.getOrDefault("description", ""));
        entry.put("status", request.getOrDefault("status", "NEW"));
        entry.put("indexedAt", System.currentTimeMillis());

        index.put(id, entry);
        logger.info("Indexed ticket #{}: '{}'", ticketId, request.get("title"));

        return ResponseEntity.noContent().build();
    }

    /**
     * Busca tickets por texto libre en título o descripción.
     * Query param: q (opcional)
     */
    @GetMapping
    public List<Map<String, Object>> search(@RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) {
            return new ArrayList<>(index.values());
        }
        String term = q.toLowerCase();
        return index.values().stream()
                .filter(e -> {
                    String title = String.valueOf(e.getOrDefault("title", "")).toLowerCase();
                    String desc  = String.valueOf(e.getOrDefault("description", "")).toLowerCase();
                    return title.contains(term) || desc.contains(term);
                })
                .toList();
    }

    /**
     * Obtiene la entrada de índice de un ticket específico.
     */
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<Map<String, Object>> getByTicket(@PathVariable Long ticketId) {
        return index.values().stream()
                .filter(e -> ticketId.equals(e.get("ticketId")))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
