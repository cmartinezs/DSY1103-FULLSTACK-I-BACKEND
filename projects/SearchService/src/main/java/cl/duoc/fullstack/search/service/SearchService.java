package cl.duoc.fullstack.search.service;

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
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private final Map<Long, Map<String, Object>> index = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public void indexTicket(Map<String, String> request) {
        Long ticketId = Long.parseLong(request.get("ticketId"));
        Long existingId = index.values().stream()
            .filter(entry -> ticketId.equals(entry.get("ticketId")))
            .map(entry -> (Long) entry.get("id"))
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
    }

    public List<Map<String, Object>> search(String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>(index.values());
        }
        String term = query.toLowerCase();
        return index.values().stream()
            .filter(entry -> {
                String title = String.valueOf(entry.getOrDefault("title", "")).toLowerCase();
                String description = String.valueOf(entry.getOrDefault("description", "")).toLowerCase();
                return title.contains(term) || description.contains(term);
            })
            .toList();
    }

    public Optional<Map<String, Object>> getByTicket(Long ticketId) {
        return index.values().stream()
            .filter(entry -> ticketId.equals(entry.get("ticketId")))
            .findFirst();
    }
}
