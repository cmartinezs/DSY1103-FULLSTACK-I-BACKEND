package cl.duoc.fullstack.sla.controller;

import cl.duoc.fullstack.sla.service.SlaService;
import java.util.List;
import java.util.Map;
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

    private final SlaService slaService;

    public SlaController(SlaService slaService) {
        this.slaService = slaService;
    }

    /**
     * Inicia el SLA de un ticket.
     * Body: { ticketId, priority }  (priority: HIGH | MEDIUM | LOW)
     * Response: { id, ticketId, priority, deadline, status, startedAt }
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startSla(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(slaService.startSla(request));
    }

    /**
     * Obtiene el SLA activo de un ticket.
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<Map<String, Object>> getSla(@PathVariable Long ticketId) {
        return slaService.getSla(ticketId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cierra el SLA de un ticket (al resolver o cerrar el ticket).
     */
    @PutMapping("/{ticketId}/close")
    public ResponseEntity<Map<String, Object>> closeSla(@PathVariable Long ticketId) {
        return slaService.closeSla(ticketId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lista todos los registros SLA.
     */
    @GetMapping
    public List<Map<String, Object>> listAll() {
        return slaService.listAll();
    }
}
