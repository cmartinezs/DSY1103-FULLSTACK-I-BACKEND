package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.TicketRequest;
import cl.duoc.fullstack.tickets.dto.TicketResult;
import cl.duoc.fullstack.tickets.dto.CategoryResult;
import cl.duoc.fullstack.tickets.dto.TagResult;
import cl.duoc.fullstack.tickets.dto.UserResult;
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
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final TagRepository tagRepository;

  public TicketService(
      TicketRepository repository,
      UserRepository userRepository,
      CategoryRepository categoryRepository,
      TagRepository tagRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
    this.categoryRepository = categoryRepository;
    this.tagRepository = tagRepository;
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

    User creator = userRepository.findByEmail(request.createdByName()).orElse(null);
    if (creator == null) {
      throw new IllegalArgumentException("Usuario creador no encontrado: " + request.createdByName());
    }

    User assignedTo = null;
    if (request.assignedToId() != null) {
      assignedTo = userRepository.findById(request.assignedToId()).orElse(null);
      if (assignedTo != null && assignedTo.getId().equals(creator.getId())) {
        throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
      }
    }

    Category category = null;
    if (request.categoryId() != null) {
      category = categoryRepository.findById(request.categoryId()).orElse(null);
    }

    List<Tag> tags = new ArrayList<>();
    if (request.tagIds() != null) {
      tags = tagRepository.findAllById(request.tagIds());
    }

    Ticket ticket = new Ticket();
    ticket.setTitle(request.title());
    ticket.setDescription(request.description());
    ticket.setCreatedBy(creator);
    ticket.setAssignedTo(assignedTo);
    ticket.setCategory(category);
    ticket.setTags(tags);
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

    if (request.assignedToId() != null) {
      User assignedTo = userRepository.findById(request.assignedToId()).orElse(null);
      if (assignedTo != null && toUpdate.getCreatedBy() != null
          && assignedTo.getId().equals(toUpdate.getCreatedBy().getId())) {
        throw new IllegalArgumentException("El creador y el asignado no pueden ser el mismo usuario");
      }
      toUpdate.setAssignedTo(assignedTo);
    }

    if (request.categoryId() != null) {
      Category category = categoryRepository.findById(request.categoryId()).orElse(null);
      toUpdate.setCategory(category);
    }

    if (request.tagIds() != null) {
      List<Tag> tags = tagRepository.findAllById(request.tagIds());
      toUpdate.setTags(tags);
    }

    toUpdate.setTitle(request.title());
    toUpdate.setDescription(request.description());
    if (request.status() != null && !request.status().isBlank()) {
      toUpdate.setStatus(request.status());
    }
    toUpdate.setEffectiveResolutionDate(request.effectiveResolutionDate());
    Ticket saved = this.repository.save(toUpdate);
    return Optional.of(toResult(saved));
  }

  private TicketResult toResult(Ticket ticket) {
    UserResult createdByResult = null;
    if (ticket.getCreatedBy() != null) {
      createdByResult = new UserResult(
          ticket.getCreatedBy().getId(),
          ticket.getCreatedBy().getName(),
          ticket.getCreatedBy().getEmail()
      );
    }

    UserResult assignedToResult = null;
    if (ticket.getAssignedTo() != null) {
      assignedToResult = new UserResult(
          ticket.getAssignedTo().getId(),
          ticket.getAssignedTo().getName(),
          ticket.getAssignedTo().getEmail()
      );
    }

    CategoryResult categoryResult = null;
    if (ticket.getCategory() != null) {
      categoryResult = new CategoryResult(
          ticket.getCategory().getId(),
          ticket.getCategory().getName(),
          ticket.getCategory().getDescription()
      );
    }

    List<TagResult> tagResults = null;
    if (ticket.getTags() != null && !ticket.getTags().isEmpty()) {
      tagResults = ticket.getTags().stream()
          .map(tag -> new TagResult(tag.getId(), tag.getName(), tag.getColor()))
          .toList();
    }

    return new TicketResult(
        ticket.getId(),
        ticket.getTitle(),
        ticket.getDescription(),
        ticket.getStatus(),
        ticket.getCreatedAt(),
        ticket.getEstimatedResolutionDate(),
        ticket.getEffectiveResolutionDate(),
        createdByResult,
        assignedToResult,
        categoryResult,
        tagResults
    );
  }
}