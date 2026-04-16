package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

  public List<Ticket> getTickets(String statusFilter) {
    return this.repository.getAll(statusFilter);
  }

  public Ticket create(Ticket ticket) {
    boolean exists = this.repository.existsByTitle(ticket.getTitle());
    if (exists) {
      throw new IllegalArgumentException(
          "Ya existe un ticket con el título: \"" + ticket.getTitle() + "\"");
    }

    if (ticket.getAssignedTo() != null 
        && ticket.getAssignedTo().equals(ticket.getCreatedBy())) {
      throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
    }

    LocalDateTime now = LocalDateTime.now();
    LocalDate ldNow = LocalDate.now();
    LocalDate estimated = ldNow.plusDays(5L);

    ticket.setStatus("NEW");
    ticket.setCreatedAt(now);
    ticket.setEstimatedResolutionDate(estimated);
    return this.repository.save(ticket);
  }

  public Ticket getById(Long id) {
    return repository.getById(id);
  }

  public Ticket deleteById(Long id) {
    return repository.deleteById(id);
  }

  public Ticket updateById(Long id, Ticket ticket) {
    Ticket toUpdate = this.repository.getById(id);
    if (toUpdate == null) {
      return null;
    }

    if (ticket.getAssignedTo() != null 
        && ticket.getAssignedTo().equals(toUpdate.getCreatedBy())) {
      throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
    }

    toUpdate.setTitle(ticket.getTitle());
    toUpdate.setDescription(ticket.getDescription());
    toUpdate.setStatus(ticket.getStatus());
    toUpdate.setEffectiveResolutionDate(ticket.getEffectiveResolutionDate());
    if (ticket.getAssignedTo() != null) {
      toUpdate.setAssignedTo(ticket.getAssignedTo());
    }
    this.repository.update(toUpdate);
    return toUpdate;
  }
}
