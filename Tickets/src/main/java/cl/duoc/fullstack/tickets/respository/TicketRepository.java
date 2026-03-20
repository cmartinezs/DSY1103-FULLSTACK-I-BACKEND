package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.Ticket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class TicketRepository {

  List<Ticket> tickets;

  long currentId = 0L;

  public TicketRepository() {
    tickets = new ArrayList<>();
    tickets.add(new Ticket(currentId++,
        "Ticket 1",
        "Ticket 1",
        "NEW",
        LocalDateTime.now(),
        null,
        null));
    tickets.add(new Ticket(currentId++,
        "Ticket 2",
        "Ticket 2",
        "NEW",
        LocalDateTime.now(),
        null,
        null));
  }

  public List<Ticket> getAll() {
    return tickets;
  }

  public Ticket save(Ticket newTicket) {
    newTicket.setId(currentId++);
    tickets.add(newTicket);
    return newTicket;
  }

  public boolean existsByTitle(String aTitle) {
    for (Ticket ticket : tickets) {
      if (ticket.getTitle().equals(aTitle)) {
        return true;
      }
    }
    return false;
  }
}
