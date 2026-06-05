package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.TicketCommand;
import cl.duoc.fullstack.tickets.dto.TicketHistoryResult;
import cl.duoc.fullstack.tickets.dto.TicketResult;
import cl.duoc.fullstack.tickets.dto.UserResult;
import cl.duoc.fullstack.tickets.exception.BadRequestException;
import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.model.TicketHistory;
import cl.duoc.fullstack.tickets.model.User;
import cl.duoc.fullstack.tickets.respository.TicketHistoryRepository;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import cl.duoc.fullstack.tickets.respository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

  private final TicketRepository repository;
  private final UserRepository userRepository;
  private final TicketHistoryRepository historyRepository;

  public TicketService(
      TicketRepository repository,
      UserRepository userRepository,
      TicketHistoryRepository historyRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
    this.historyRepository = historyRepository;
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

    recordChange(saved, null, "NEW", null, null, "Ticket creado");

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

  public TicketResult updateById(Long id, TicketCommand command) {
    Ticket ticket = repository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Ticket con id " + id + " no existe"));

    String previousStatus = ticket.getStatus();
    String previousAssignedEmail = ticket.getAssignedTo() != null
        ? ticket.getAssignedTo().getEmail()
        : null;

    ticket.setTitle(command.title());
    ticket.setDescription(command.description());
    if (command.status() != null && !command.status().isBlank()) {
      ticket.setStatus(command.status());
    }
    ticket.setEffectiveResolutionDate(command.effectiveResolutionDate());

    Ticket saved = repository.save(ticket);

    recordChange(saved, previousStatus, saved.getStatus(), previousAssignedEmail, previousAssignedEmail, null);

    return toResult(saved);
  }

  public Optional<TicketResult> assignTicket(Long ticketId, String assignedToEmail) {
    Optional<Ticket> ticketOpt = repository.findById(ticketId);
    if (ticketOpt.isEmpty()) {
      return Optional.empty();
    }

    Ticket ticket = ticketOpt.get();
    String previousAssignedEmail = ticket.getAssignedTo() != null
        ? ticket.getAssignedTo().getEmail()
        : null;
    String newAssignedEmail;

    if (assignedToEmail == null || assignedToEmail.isBlank()) {
      ticket.setAssignedTo(null);
      newAssignedEmail = null;
    } else {
      User assignee = userRepository.findByEmail(assignedToEmail)
          .orElseThrow(() -> new BadRequestException(
              "El email '" + assignedToEmail + "' no existe en el sistema"));
      ticket.setAssignedTo(assignee);
      newAssignedEmail = assignee.getEmail();
    }

    Ticket saved = repository.save(ticket);

    recordChange(saved, null, null, previousAssignedEmail, newAssignedEmail, null);

    return Optional.of(toResult(saved));
  }

  public Optional<List<TicketHistoryResult>> getHistory(Long ticketId) {
    if (!repository.existsById(ticketId)) {
      return Optional.empty();
    }
    List<TicketHistoryResult> history = historyRepository
        .findByTicketIdOrderByChangedAtDesc(ticketId)
        .stream()
        .map(this::toHistoryResult)
        .toList();
    return Optional.of(history);
  }

  private void recordChange(
      Ticket ticket,
      String previousStatus,
      String newStatus,
      String previousAssignedEmail,
      String newAssignedEmail,
      String comment) {

    boolean statusChanged = newStatus != null
        && !newStatus.equalsIgnoreCase(previousStatus == null ? "" : previousStatus);
    boolean assigneeChanged = !Objects.equals(previousAssignedEmail, newAssignedEmail);

    if (!statusChanged && !assigneeChanged) {
      return;
    }

    TicketHistory entry = new TicketHistory();
    entry.setTicket(ticket);
    entry.setPreviousStatus(statusChanged ? previousStatus : null);
    entry.setNewStatus(statusChanged ? newStatus : null);
    entry.setPreviousAssignedEmail(assigneeChanged ? previousAssignedEmail : null);
    entry.setNewAssignedEmail(assigneeChanged ? newAssignedEmail : null);
    entry.setChangedAt(LocalDateTime.now());
    entry.setComment(comment);
    historyRepository.save(entry);
  }

  private TicketHistoryResult toHistoryResult(TicketHistory h) {
    return new TicketHistoryResult(
        h.getId(),
        h.getPreviousStatus(),
        h.getNewStatus(),
        h.getPreviousAssignedEmail(),
        h.getNewAssignedEmail(),
        h.getChangedAt(),
        h.getComment()
    );
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

