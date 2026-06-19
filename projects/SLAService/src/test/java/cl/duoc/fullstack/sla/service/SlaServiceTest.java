package cl.duoc.fullstack.sla.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SlaServiceTest {

    private final SlaService service = new SlaService();

    @Test
    void startSla_shouldCreateHighPriorityDeadline() {
        Instant before = Instant.now();

        Map<String, Object> result = service.startSla(Map.of("ticketId", "1", "priority", "HIGH"));

        long hours = Duration.between(before, Instant.parse((String) result.get("deadline"))).toHours();
        assertThat(result)
            .containsEntry("ticketId", 1L)
            .containsEntry("priority", "HIGH")
            .containsEntry("status", "OPEN");
        assertThat(hours).isBetween(23L, 24L);
    }

    @Test
    void startSla_shouldReturnExistingOpenSla_forSameTicket() {
        Map<String, Object> first = service.startSla(Map.of("ticketId", "1", "priority", "LOW"));
        Map<String, Object> second = service.startSla(Map.of("ticketId", "1", "priority", "HIGH"));

        assertThat(second.get("id")).isEqualTo(first.get("id"));
        assertThat(service.listAll()).hasSize(1);
    }

    @Test
    void closeSla_shouldCloseExistingRecord() {
        service.startSla(Map.of("ticketId", "1"));

        assertThat(service.closeSla(1L)).isPresent();
        assertThat(service.getSla(1L).orElseThrow())
            .containsEntry("status", "CLOSED")
            .containsKey("closedAt");
    }

    @Test
    void closeSla_shouldReturnEmpty_whenTicketHasNoSla() {
        assertThat(service.closeSla(99L)).isEmpty();
    }

    @Test
    void listAll_shouldReturnCreatedRecords() {
        service.startSla(Map.of("ticketId", "1"));
        service.startSla(Map.of("ticketId", "2"));

        assertThat(service.listAll()).hasSize(2);
    }
}
