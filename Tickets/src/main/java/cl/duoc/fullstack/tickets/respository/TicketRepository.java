package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.Ticket;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class TicketRepository {
  List<Ticket> tickets;

  public TicketRepository(){
    tickets = new ArrayList<>();
    tickets.add(new Ticket(1L, "Ticket 1", "Ticket 1", "NEW"));
    tickets.add(new Ticket(2L, "Ticket 2", "Ticket 2", "NEW"));
  }

  public List<Ticket> getAll(){
    return tickets;
  }

}
