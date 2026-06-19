package cl.duoc.fullstack.notifications.controller;

import cl.duoc.fullstack.notifications.service.NotificationService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public Map<String, Object> createNotification(@RequestBody Map<String, String> request) {
        return notificationService.createNotification(request);
    }

    @GetMapping
    public List<Map<String, Object>> listNotifications() {
        return notificationService.listNotifications();
    }

    @GetMapping("/{id}")
    public Map<String, Object> getNotification(@PathVariable Long id) {
        return notificationService.getNotification(id);
    }
}
