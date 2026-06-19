package cl.duoc.fullstack.audit.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class AuditServiceTest {

    private final AuditService service = new AuditService();

    @Test
    void logEvent_shouldCreateAuditRecord_whenRequestIsValid() {
        Map<String, Object> result = service.logEvent(Map.of(
            "action", "TICKET_CREATED",
            "entityId", "10",
            "username", "ana"
        ));

        assertThat(result)
            .containsEntry("id", 1L)
            .containsEntry("action", "TICKET_CREATED")
            .containsEntry("entityId", 10L)
            .containsEntry("username", "ana");
    }

    @Test
    void listAuditLogs_shouldReturnCreatedRecords() {
        service.logEvent(Map.of("action", "CREATE", "entityId", "1"));
        service.logEvent(Map.of("action", "UPDATE", "entityId", "2"));

        assertThat(service.listAuditLogs()).hasSize(2);
    }

    @Test
    void getAuditByTicket_shouldFilterByTicketId() {
        service.logEvent(Map.of("action", "CREATE", "entityId", "7"));
        service.logEvent(Map.of("action", "UPDATE", "entityId", "8"));

        assertThat(service.getAuditByTicket(7L))
            .singleElement()
            .satisfies(event -> assertThat(event).containsEntry("entityId", 7L));
    }
}
