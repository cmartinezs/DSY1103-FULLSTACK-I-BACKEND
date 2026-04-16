package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.TicketRequest;
import cl.duoc.fullstack.tickets.dto.TicketResult;
import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

  private final TicketRepository repository;

  public TicketService(TicketRepository repository) {
    this.repository = repository;
  }

  public List<TicketResult> getTickets() {
    return this.repository.findAllOrderByCreatedAt().stream()
        .map(this::toResult)
        .toList();
  }

  public List<TicketResult> getTickets(String statusFilter) {
    if (statusFilter == null || statusFilter.isBlank()) {
      return getTickets();
    }
    return this.repository.findAllByStatusIgnoreCase(statusFilter).stream()
        .map(this::toResult)
        .toList();
  }

  public TicketResult create(TicketRequest request) {
    boolean exists = this.repository.existsByTitleIgnoreCase(request.title());
    if (exists) {
      throw new IllegalArgumentException(
          "Ya existe un ticket con el título: \"" + request.title() + "\"");
    }

    if (request.assignedTo() != null
        && request.assignedTo().equals(request.createdBy())) {
      throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
    }

    Ticket ticket = new Ticket();
    ticket.setTitle(request.title());
    ticket.setDescription(request.description());
    ticket.setCreatedBy(request.createdBy());
    ticket.setAssignedTo(request.assignedTo());
    ticket.setStatus("NEW");
    ticket.setCreatedAt(LocalDateTime.now());
    ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5));
    Ticket saved = this.repository.save(ticket);
    return toResult(saved);
  }

  public Optional<TicketResult> getById(Long id) {
    return this.repository.findById(id).map(this::toResult);
  }

  public boolean deleteById(Long id) {
    if (this.repository.existsById(id)) {
      this.repository.deleteById(id);
      return true;
    }
    return false;
  }

  public Optional<TicketResult> updateById(Long id, TicketRequest request) {
    Optional<Ticket> found = this.repository.findById(id);
    if (found.isEmpty()) {
      return Optional.empty();
    }

    Ticket toUpdate = found.get();

    if (request.assignedTo() != null
        && request.assignedTo().equals(toUpdate.getCreatedBy())) {
      throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
    }

    toUpdate.setTitle(request.title());
    toUpdate.setDescription(request.description());
    if (request.status() != null && !request.status().isBlank()) {
      toUpdate.setStatus(request.status());
    }
    toUpdate.setEffectiveResolutionDate(request.effectiveResolutionDate());
    if (request.assignedTo() != null) {
      toUpdate.setAssignedTo(request.assignedTo());
    }
    Ticket saved = this.repository.save(toUpdate);
    return Optional.of(toResult(saved));
  }

  private TicketResult toResult(Ticket ticket) {
    return new TicketResult(
        ticket.getId(),
        ticket.getTitle(),
        ticket.getDescription(),
        ticket.getStatus(),
        ticket.getCreatedAt(),
        ticket.getEstimatedResolutionDate(),
        ticket.getEffectiveResolutionDate(),
        ticket.getCreatedBy(),
        ticket.getAssignedTo()
    );
  }
}