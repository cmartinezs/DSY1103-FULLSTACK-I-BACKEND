package cl.duoc.fullstack.tickets.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "notificationService",
    url = "${notification.service.url:http://localhost:8081}",
    fallback = NotificationClientFallback.class
)
public interface NotificationClient {

    @PostMapping("/api/notifications")
    Map<String, Object> createNotification(@RequestBody Map<String, String> notification);

    @GetMapping("/api/notifications")
    List<Map<String, Object>> listNotifications();

    @GetMapping("/api/notifications/{id}")
    Map<String, Object> getNotification(@PathVariable("id") Long id);
}