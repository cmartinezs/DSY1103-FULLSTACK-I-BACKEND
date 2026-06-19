package cl.duoc.fullstack.search.controller;

import cl.duoc.fullstack.search.service.SearchService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Indexa o reindexar un ticket.
     * Body: { ticketId, title, description, status }
     * Response: 204 No Content
     */
    @PostMapping("/index")
    public ResponseEntity<Void> indexTicket(@RequestBody Map<String, String> request) {
        searchService.indexTicket(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca tickets por texto libre en título o descripción.
     * Query param: q (opcional)
     */
    @GetMapping
    public List<Map<String, Object>> search(@RequestParam(required = false) String q) {
        return searchService.search(q);
    }

    /**
     * Obtiene la entrada de índice de un ticket específico.
     */
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<Map<String, Object>> getByTicket(@PathVariable Long ticketId) {
        return searchService.getByTicket(ticketId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
