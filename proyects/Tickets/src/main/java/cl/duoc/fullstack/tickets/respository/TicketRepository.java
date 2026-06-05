package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.Ticket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TicketRepository {

  private final Map<Long, Ticket> db = new HashMap<>();
  private long currentId = 1L;

  public TicketRepository() {
    LocalDateTime now = LocalDateTime.now();
    LocalDate estimated = LocalDate.now().plusDays(5);

    Ticket t1 = new Ticket(currentId, "Ticket 1", "Descripción del ticket 1", "NEW", now, estimated, null, "admin", null);
    db.put(currentId++, t1);

    Ticket t2 = new Ticket(currentId, "Ticket 2", "Descripción del ticket 2", "NEW", now, estimated, null, "admin", null);
    db.put(currentId++, t2);

    Ticket t3 = new Ticket(currentId, "Ticket 3", "Descripción del ticket 3 en proceso", "IN_PROGRESS", now.minusDays(2), estimated.minusDays(2), now, "admin", "juan");
    db.put(currentId++, t3);

    Ticket t4 = new Ticket(currentId, "Ticket 4", "Descripción del ticket 4 resuelto", "RESOLVED", now.minusDays(5), estimated.minusDays(5), now.minusDays(3), "admin", "juan");
    db.put(currentId++, t4);

    Ticket t5 = new Ticket(currentId, "Ticket 5", "Descripción del ticket 5 cerrado", "CLOSED", now.minusDays(10), estimated.minusDays(8), now.minusDays(7), "admin", "juan");
    db.put(currentId++, t5);
  }

  public List<Ticket> getAll() {
    return db.values().stream()
        .sorted(Comparator.comparing(Ticket::getCreatedAt))
        .toList();
  }

  public List<Ticket> getAll(String statusFilter) {
    if (statusFilter == null || statusFilter.isBlank()) {
      return getAll();
    }
    return db.values().stream()
        .filter(t -> t.getStatus().equalsIgnoreCase(statusFilter))
        .sorted(Comparator.comparing(Ticket::getCreatedAt))
        .toList();
  }

  public Ticket save(Ticket newTicket) {
    newTicket.setId(currentId);
    db.put(currentId++, newTicket);
    return newTicket;
  }

  public boolean existsByTitle(String title) {
    return db.values().stream()
        .anyMatch(t -> t.getTitle().equalsIgnoreCase(title));
  }

  public Optional<Ticket> findById(Long id) {
    return Optional.ofNullable(db.get(id));
  }

  public boolean deleteById(Long id) {
    return db.remove(id) != null;
  }

  public void update(Ticket toUpdate) {
    db.put(toUpdate.getId(), toUpdate);
  }
}
