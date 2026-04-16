package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.TicketRequest;
import cl.duoc.fullstack.tickets.dto.TicketResult;
import cl.duoc.fullstack.tickets.dto.CategoryResult;
import cl.duoc.fullstack.tickets.dto.TagResult;
import cl.duoc.fullstack.tickets.dto.TicketHistoryResult;
import cl.duoc.fullstack.tickets.dto.UserResult;
import cl.duoc.fullstack.tickets.model.Category;
import cl.duoc.fullstack.tickets.model.Tag;
import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.model.TicketHistory;
import cl.duoc.fullstack.tickets.model.User;
import cl.duoc.fullstack.tickets.respository.CategoryRepository;
import cl.duoc.fullstack.tickets.respository.TagRepository;
import cl.duoc.fullstack.tickets.respository.TicketHistoryRepository;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import cl.duoc.fullstack.tickets.respository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

  private final TicketRepository repository;
  private final CategoryRepository categoryRepository;
  private final TagRepository tagRepository;
  private final UserRepository userRepository;
  private final TicketHistoryRepository historyRepository;

  public TicketService(
      TicketRepository repository,
      CategoryRepository categoryRepository,
      TagRepository tagRepository,
      UserRepository userRepository,
      TicketHistoryRepository historyRepository) {
    this.repository = repository;
    this.categoryRepository = categoryRepository;
    this.tagRepository = tagRepository;
    this.userRepository = userRepository;
    this.historyRepository = historyRepository;
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

    Ticket ticket = new Ticket();
    ticket.setTitle(request.title());
    ticket.setDescription(request.description());
    ticket.setStatus("NEW");
    ticket.setCreatedAt(LocalDateTime.now());
    ticket.setEstimatedResolutionDate(LocalDate.now().plusDays(5));

    if (request.createdById() != null) {
      User creator = userRepository.findById(request.createdById())
          .orElseThrow(() -> new IllegalArgumentException(
              "No existe un usuario con ID " + request.createdById()));
      ticket.setCreatedBy(creator);
    }

    if (request.assignedToId() != null) {
      User assignee = userRepository.findById(request.assignedToId())
          .orElseThrow(() -> new IllegalArgumentException(
              "No existe un usuario con ID " + request.assignedToId()));
      if (request.createdById() != null
          && request.assignedToId().equals(request.createdById())) {
        throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
      }
      ticket.setAssignedTo(assignee);
    }

    if (request.categoryId() != null) {
      Optional<Category> category = categoryRepository.findById(request.categoryId());
      category.ifPresentOrElse(
          ticket::setCategory,
          () -> {
            throw new IllegalArgumentException("Category not found");
          });
    }

    if (request.tagIds() != null && !request.tagIds().isEmpty()) {
      List<Tag> tags = tagRepository.findAllById(request.tagIds());
      ticket.setTags(tags);
    } else {
      ticket.setTags(new ArrayList<>());
    }

    Ticket saved = this.repository.save(ticket);

    registrarHistorial(saved, null, "NEW", "Ticket creado");

    return toResult(saved);
  }

  public Optional<TicketResult> getById(Long id) {
    return this.repository.findById(id).map(this::toResult);
  }

  public boolean existsById(Long id) {
    return this.repository.existsById(id);
  }

  public List<TicketHistoryResult> getHistory(Long ticketId) {
    return this.historyRepository.findByTicketIdOrderByChangedAtDesc(ticketId).stream()
        .map(this::toHistoryResult)
        .toList();
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

    toUpdate.setTitle(request.title());
    toUpdate.setDescription(request.description());
    if (request.status() != null && !request.status().isBlank()
        && !request.status().equalsIgnoreCase(toUpdate.getStatus())) {
      String estadoAnterior = toUpdate.getStatus();
      toUpdate.setStatus(request.status());
      registrarHistorial(toUpdate, estadoAnterior, request.status(), null);
    }
    toUpdate.setEffectiveResolutionDate(request.effectiveResolutionDate());

    if (request.assignedToId() != null) {
      User assignee = userRepository.findById(request.assignedToId())
          .orElseThrow(() -> new IllegalArgumentException(
              "No existe un usuario con ID " + request.assignedToId()));
      User creator = toUpdate.getCreatedBy();
      if (creator != null && request.assignedToId().equals(creator.getId())) {
        throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
      }
      toUpdate.setAssignedTo(assignee);
    }

    if (request.categoryId() != null) {
      Optional<Category> category = categoryRepository.findById(request.categoryId());
      category.ifPresentOrElse(
          toUpdate::setCategory,
          () -> {
            throw new IllegalArgumentException("Category not found");
          });
    }

    if (request.tagIds() != null && !request.tagIds().isEmpty()) {
      List<Tag> tags = tagRepository.findAllById(request.tagIds());
      toUpdate.setTags(tags);
    }

    Ticket saved = this.repository.save(toUpdate);
    return Optional.of(toResult(saved));
  }

  private TicketResult toResult(Ticket ticket) {
    UserResult createdBy = ticket.getCreatedBy() != null
        ? new UserResult(ticket.getCreatedBy().getId(), ticket.getCreatedBy().getName(), ticket.getCreatedBy().getEmail())
        : null;
    UserResult assignedTo = ticket.getAssignedTo() != null
        ? new UserResult(ticket.getAssignedTo().getId(), ticket.getAssignedTo().getName(), ticket.getAssignedTo().getEmail())
        : null;
    CategoryResult category = ticket.getCategory() != null
        ? new CategoryResult(ticket.getCategory().getId(), ticket.getCategory().getName(), ticket.getCategory().getDescription())
        : null;
    List<TagResult> tags = ticket.getTags() != null
        ? ticket.getTags().stream()
            .map(tag -> new TagResult(tag.getId(), tag.getName(), tag.getColor()))
            .toList()
        : List.of();

    return new TicketResult(
        ticket.getId(),
        ticket.getTitle(),
        ticket.getDescription(),
        ticket.getStatus(),
        ticket.getCreatedAt(),
        ticket.getEstimatedResolutionDate(),
        ticket.getEffectiveResolutionDate(),
        createdBy,
        assignedTo,
        category,
        tags
    );
  }

  private TicketHistoryResult toHistoryResult(TicketHistory history) {
    return new TicketHistoryResult(
        history.getId(),
        history.getPreviousStatus(),
        history.getNewStatus(),
        history.getChangedAt(),
        history.getComment()
    );
  }

  private void registrarHistorial(Ticket ticket, String estadoAnterior, String estadoNuevo, String comentario) {
    TicketHistory entrada = new TicketHistory();
    entrada.setTicket(ticket);
    entrada.setPreviousStatus(estadoAnterior);
    entrada.setNewStatus(estadoNuevo);
    entrada.setChangedAt(LocalDateTime.now());
    entrada.setComment(comentario);
    this.historyRepository.save(entrada);
  }
}