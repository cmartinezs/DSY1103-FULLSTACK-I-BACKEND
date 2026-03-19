package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
  private TicketRepository repository;

  public TicketService(TicketRepository repository) {
    this.repository = repository;
  }

  public List<Ticket> getTickets() {
    return this.repository.getAll();
  }
}
