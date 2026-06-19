package cl.duoc.fullstack.notifications.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final Map<Long, Map<String, Object>> notifications = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Map<String, Object> createNotification(Map<String, String> request) {
        Long id = idCounter.getAndIncrement();
        Map<String, Object> notification = Map.of(
            "id", id,
            "title", request.get("title"),
            "message", request.get("message"),
            "type", request.getOrDefault("type", "INFO"),
            "recipient", request.getOrDefault("recipient", "all"),
            "sent", false,
            "timestamp", System.currentTimeMillis()
        );
        notifications.put(id, new ConcurrentHashMap<>(notification));
        logger.info("Notification created: {} - {}", id, request.get("title"));
        return notification;
    }

    public List<Map<String, Object>> listNotifications() {
        return new ArrayList<>(notifications.values());
    }

    public Map<String, Object> getNotification(Long id) {
        Map<String, Object> notification = notifications.get(id);
        return notification != null
            ? notification
            : Map.of("error", "Notification not found", "id", id);
    }
}
