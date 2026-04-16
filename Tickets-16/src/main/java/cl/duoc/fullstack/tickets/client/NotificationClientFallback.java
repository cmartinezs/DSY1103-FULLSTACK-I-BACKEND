package cl.duoc.fullstack.tickets.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationClientFallback implements NotificationClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationClientFallback.class);

    @Override
    public Map<String, Object> createNotification(Map<String, String> notification) {
        logger.warn("Notification service unavailable. Notification not created: {}", notification.get("title"));
        return Collections.singletonMap("status", "fallback");
    }

    @Override
    public List<Map<String, Object>> listNotifications() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> getNotification(Long id) {
        return Map.of("error", "Notification service unavailable", "id", id);
    }
}