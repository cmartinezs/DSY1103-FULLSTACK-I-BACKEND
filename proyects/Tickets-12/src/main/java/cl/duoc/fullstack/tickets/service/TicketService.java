package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.TicketCommand;
import cl.duoc.fullstack.tickets.dto.TicketResult;
import cl.duoc.fullstack.tickets.dto.UserResult;
import cl.duoc.fullstack.tickets.exception.BadRequestException;
import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.model.User;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import cl.duoc.fullstack.tickets.respository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

  private final TicketRepository repository;
  private final UserRepository userRepository;

  public TicketService(TicketRepository repository, UserRepository userRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
  }

  public List<TicketResult> getTickets() {
    return this.repository.findAllByOrderByCreatedAtAsc().stream()
        .map(this::toResult)
        .toList();
  }

  public List<TicketResult> getTickets(String statusFilter) {
    if (statusFilter == null || statusFilter.isBlank()) {
      return getTickets();
    }
    return this.repository.findByStatusIgnoreCase(statusFilter).stream()
        .map(this::toResult)
        .toList();
  }

  public TicketResult create(TicketCommand command) {
    boolean exists = this.repository.existsByTitleIgnoreCase(command.title());
    if (exists) {
      throw new IllegalArgumentException(
          "Ya existe un ticket con el título: \"" + command.title() + "\"");
    }

    User creator = userRepository.findByEmail(command.createdByEmail())
        .orElseThrow(() -> new BadRequestException(
            "El email '" + command.createdByEmail() + "' no existe en el sistema"));

    Ticket ticket = new Ticket();
    ticket.setTitle(command.title());
    ticket.setDescription(command.description());
    ticket.setStatus("NEW");
    ticket.setCreatedAt(LocalDateTime.now());
    ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5));
    ticket.setCreatedBy(creator);
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

  public Optional<TicketResult> updateById(Long id, TicketCommand command) {
    Optional<Ticket> found = this.repository.findById(id);
    if (found.isEmpty()) {
      return Optional.empty();
    }

    Ticket toUpdate = found.get();
    toUpdate.setTitle(command.title());
    toUpdate.setDescription(command.description());
    if (command.status() != null && !command.status().isBlank()) {
      toUpdate.setStatus(command.status());
    }
    toUpdate.setEffectiveResolutionDate(command.effectiveResolutionDate());
    Ticket saved = this.repository.save(toUpdate);
    return Optional.of(toResult(saved));
  }

  public Optional<TicketResult> assignTicket(Long ticketId, String assignedToEmail) {
    if (assignedToEmail == null || assignedToEmail.isBlank()) {
      Optional<Ticket> ticketOpt = repository.findById(ticketId);
      if (!ticketOpt.isPresent()) {
        return Optional.empty();
      }
      Ticket ticket = ticketOpt.get();
      ticket.setAssignedTo(null);
      return Optional.of(toResult(repository.save(ticket)));
    }

    User assignee = userRepository.findByEmail(assignedToEmail)
        .orElseThrow(() -> new BadRequestException(
            "El email '" + assignedToEmail + "' no existe en el sistema"));

    Optional<Ticket> ticketOpt = repository.findById(ticketId);
    if (!ticketOpt.isPresent()) {
      return Optional.empty();
    }

    Ticket ticket = ticketOpt.get();
    ticket.setAssignedTo(assignee);
    return Optional.of(toResult(repository.save(ticket)));
  }

  private TicketResult toResult(Ticket ticket) {
    UserResult createdBy = ticket.getCreatedBy() != null
        ? new UserResult(ticket.getCreatedBy().getId(),
                         ticket.getCreatedBy().getName(),
                         ticket.getCreatedBy().getEmail())
        : null;
    UserResult assignedTo = ticket.getAssignedTo() != null
        ? new UserResult(ticket.getAssignedTo().getId(),
                         ticket.getAssignedTo().getName(),
                         ticket.getAssignedTo().getEmail())
        : null;
    return new TicketResult(
        ticket.getId(),
        ticket.getTitle(),
        ticket.getDescription(),
        ticket.getStatus(),
        ticket.getCreatedAt(),
        ticket.getEstimatedResolutionDate(),
        ticket.getEffectiveResolutionDate(),
        createdBy,
        assignedTo
    );
  }
}

