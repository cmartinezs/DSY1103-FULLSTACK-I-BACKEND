package cl.duoc.fullstack.notifications.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final Map<Long, Map<String, Object>> notifications = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @PostMapping
    public Map<String, Object> createNotification(@RequestBody Map<String, String> request) {
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

    @GetMapping
    public List<Map<String, Object>> listNotifications() {
        return new ArrayList<>(notifications.values());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getNotification(@PathVariable Long id) {
        Map<String, Object> notification = notifications.get(id);
        if (notification == null) {
            return Map.of("error", "Notification not found", "id", id);
        }
        return notification;
    }
}