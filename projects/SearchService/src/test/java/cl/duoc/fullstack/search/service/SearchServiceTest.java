package cl.duoc.fullstack.search.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class SearchServiceTest {

    private final SearchService service = new SearchService();

    @Test
    void indexTicket_shouldCreateSearchEntry() {
        service.indexTicket(Map.of(
            "ticketId", "10",
            "title", "Error de acceso",
            "description", "No puedo ingresar"
        ));

        assertThat(service.getByTicket(10L)).isPresent();
        assertThat(service.getByTicket(10L).orElseThrow())
            .containsEntry("title", "Error de acceso")
            .containsEntry("status", "NEW");
    }

    @Test
    void indexTicket_shouldReplaceExistingEntry_forSameTicket() {
        service.indexTicket(Map.of("ticketId", "10", "title", "Anterior"));
        service.indexTicket(Map.of("ticketId", "10", "title", "Actualizado"));

        assertThat(service.search(null)).hasSize(1);
        assertThat(service.getByTicket(10L).orElseThrow()).containsEntry("title", "Actualizado");
    }

    @Test
    void search_shouldMatchTitleOrDescription_ignoringCase() {
        service.indexTicket(Map.of("ticketId", "1", "title", "Error LOGIN", "description", "Acceso"));
        service.indexTicket(Map.of("ticketId", "2", "title", "Impresora", "description", "Sin papel"));

        assertThat(service.search("login")).hasSize(1);
        assertThat(service.search("PAPEL")).hasSize(1);
    }

    @Test
    void getByTicket_shouldReturnEmpty_whenTicketIsNotIndexed() {
        assertThat(service.getByTicket(99L)).isEmpty();
    }
}
