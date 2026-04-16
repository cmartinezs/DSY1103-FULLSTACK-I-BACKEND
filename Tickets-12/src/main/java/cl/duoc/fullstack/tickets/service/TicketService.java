package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.TicketRequest;
import cl.duoc.fullstack.tickets.model.Category;
import cl.duoc.fullstack.tickets.model.Tag;
import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.model.User;
import cl.duoc.fullstack.tickets.respository.CategoryRepository;
import cl.duoc.fullstack.tickets.respository.TagRepository;
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

  public TicketService(
      TicketRepository repository,
      CategoryRepository categoryRepository,
      TagRepository tagRepository,
      UserRepository userRepository) {
    this.repository = repository;
    this.categoryRepository = categoryRepository;
    this.tagRepository = tagRepository;
    this.userRepository = userRepository;
  }

  public List<Ticket> getTickets() {
    return this.repository.findAllOrderByCreatedAt();
  }

  public List<Ticket> getTickets(String statusFilter) {
    if (statusFilter == null || statusFilter.isBlank()) {
      return getTickets();
    }
    return this.repository.findAllByStatusIgnoreCase(statusFilter);
  }

  public Ticket create(TicketRequest request) {
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

    return this.repository.save(ticket);
  }

  public Optional<Ticket> getById(Long id) {
    return this.repository.findById(id);
  }

  public boolean deleteById(Long id) {
    if (this.repository.existsById(id)) {
      this.repository.deleteById(id);
      return true;
    }
    return false;
  }

  public Optional<Ticket> updateById(Long id, TicketRequest request) {
    Optional<Ticket> found = this.repository.findById(id);
    if (found.isEmpty()) {
      return Optional.empty();
    }

    Ticket toUpdate = found.get();

    toUpdate.setTitle(request.title());
    toUpdate.setDescription(request.description());
    if (request.status() != null && !request.status().isBlank()) {
      toUpdate.setStatus(request.status());
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

    this.repository.save(toUpdate);
    return Optional.of(toUpdate);
  }
}
