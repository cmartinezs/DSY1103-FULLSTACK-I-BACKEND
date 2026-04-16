package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.Ticket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TicketRepository {

  private Map<Long, Ticket> db;
  private long currentId;

  public TicketRepository() {
    db = new HashMap<>();
    currentId = 1L;
    
    LocalDateTime now = LocalDateTime.now();
    LocalDate estimated = LocalDate.now().plusDays(5);

    Ticket t1 = new Ticket(currentId, "Ticket 1", "Descripción del ticket 1", "NEW", now, estimated, null, "admin", null);
    db.put(currentId++, t1);

    Ticket t2 = new Ticket(currentId, "Ticket 2", "Descripción del ticket 2", "NEW", now, estimated, null, "admin", null);
    db.put(currentId++, t2);
  }

  public List<Ticket> getAll() {
    return new ArrayList<>(db.values());
  }

  public List<Ticket> getAll(String statusFilter) {
    List<Ticket> filtered = new ArrayList<>();
    for (Ticket ticket : db.values()) {
      if (statusFilter == null || ticket.getStatus().equalsIgnoreCase(statusFilter)) {
        filtered.add(ticket);
      }
    }
    return filtered;
  }

  public Ticket save(Ticket newTicket) {
    newTicket.setId(currentId++);
    db.put(newTicket.getId(), newTicket);
    return newTicket;
  }

  public boolean existsByTitle(String aTitle) {
    for (Ticket ticket : db.values()) {
      if (ticket.getTitle().equalsIgnoreCase(aTitle)) {
        return true;
      }
    }
    return false;
  }

  public Optional<Ticket> findById(Long id) {
    return Optional.ofNullable(db.get(id));
  }

  public Ticket getById(Long id) {
    return db.get(id);
  }

  public Ticket deleteById(Long id) {
    return db.remove(id);
  }

  public void update(Ticket toUpdate) {
    db.put(toUpdate.getId(), toUpdate);
  }
}
