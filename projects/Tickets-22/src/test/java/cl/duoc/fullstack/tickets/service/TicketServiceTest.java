package cl.duoc.fullstack.tickets.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.fullstack.tickets.client.AuditClient;
import cl.duoc.fullstack.tickets.client.NotificationClient;
import cl.duoc.fullstack.tickets.dto.TicketRequest;
import cl.duoc.fullstack.tickets.dto.TicketHistoryResult;
import cl.duoc.fullstack.tickets.dto.TicketResult;
import cl.duoc.fullstack.tickets.model.Category;
import cl.duoc.fullstack.tickets.model.Tag;
import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.model.TicketHistory;
import cl.duoc.fullstack.tickets.model.User;
import java.time.LocalDateTime;
import java.util.List;
import cl.duoc.fullstack.tickets.respository.CategoryRepository;
import cl.duoc.fullstack.tickets.respository.TagRepository;
import cl.duoc.fullstack.tickets.respository.TicketHistoryRepository;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import cl.duoc.fullstack.tickets.respository.UserRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

  @Mock
  private TicketRepository ticketRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private TagRepository tagRepository;

  @Mock
  private TicketHistoryRepository historyRepository;

  @Mock
  private NotificationClient notificationClient;

  @Mock
  private AuditClient auditClient;

  @InjectMocks
  private TicketService ticketService;

  @Test
  void create_shouldSaveTicket_whenDataIsValid() {
    // given
    TicketRequest request = validRequest();
    User creator = creator();
    when(ticketRepository.existsByTitleIgnoreCase(request.title())).thenReturn(false);
    when(userRepository.findByEmail(request.createdByName())).thenReturn(Optional.of(creator));
    when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
      Ticket ticket = invocation.getArgument(0);
      ticket.setId(1L);
      return ticket;
    });

    // when
    TicketResult result = ticketService.create(request);

    // then
    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.title()).isEqualTo("Problema de acceso");
    assertThat(result.status()).isEqualTo("NEW");
    assertThat(result.createdBy().email()).isEqualTo("ana@duoc.cl");
    verify(ticketRepository).save(any(Ticket.class));
    verify(historyRepository).save(any(TicketHistory.class));
  }

  @Test
  void getById_shouldReturnTicket_whenTicketExists() {
    // given
    Ticket ticket = savedTicket();
    when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

    // when
    Optional<TicketResult> result = ticketService.getById(1L);

    // then
    assertThat(result).isPresent();
    assertThat(result.orElseThrow().title()).isEqualTo("Problema de acceso");
    verify(ticketRepository).findById(1L);
  }

  @Test
  void getById_shouldReturnEmpty_whenTicketDoesNotExist() {
    // given
    when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

    // when
    Optional<TicketResult> result = ticketService.getById(99L);

    // then
    assertThat(result).isEmpty();
    verify(ticketRepository).findById(99L);
  }

  @Test
  void create_shouldRejectBlankTitle_whenTitleIsInvalid() {
    // given
    TicketRequest request = new TicketRequest(
        "   ", "Detalle", "ana@duoc.cl", null, null, null, null, null);

    // when / then
    assertThatThrownBy(() -> ticketService.create(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("título");

    verify(ticketRepository, never()).save(any(Ticket.class));
    verify(notificationClient, never()).createNotification(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void create_shouldSendNotification_whenTicketIsCreated() {
    // given
    TicketRequest request = validRequest();
    when(ticketRepository.existsByTitleIgnoreCase(request.title())).thenReturn(false);
    when(userRepository.findByEmail(request.createdByName())).thenReturn(Optional.of(creator()));
    when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket());
    ArgumentCaptor<Map<String, String>> notificationCaptor = ArgumentCaptor.forClass(Map.class);

    // when
    ticketService.create(request);

    // then
    verify(notificationClient).createNotification(notificationCaptor.capture());
    assertThat(notificationCaptor.getValue())
        .containsEntry("title", "Ticket creado")
        .containsEntry("type", "TICKET_UPDATE");
    assertThat(notificationCaptor.getValue().get("message"))
        .contains("Ticket #1")
        .contains("Problema de acceso");
  }

  @Test
  void create_shouldNotFail_whenNotificationServiceIsDown() {
    // given
    TicketRequest request = validRequest();
    when(ticketRepository.existsByTitleIgnoreCase(request.title())).thenReturn(false);
    when(userRepository.findByEmail(request.createdByName())).thenReturn(Optional.of(creator()));
    when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket());
    when(notificationClient.createNotification(any()))
        .thenThrow(new IllegalStateException("NotificationService no disponible"));

    // when / then
    assertThatCode(() -> ticketService.create(request)).doesNotThrowAnyException();
    verify(notificationClient).createNotification(any());
  }

  @Test
  void getTickets_shouldReturnMappedTickets_withoutFilter() {
    // given
    when(ticketRepository.findAllOrderByCreatedAt()).thenReturn(List.of(savedTicket()));

    // when
    List<TicketResult> result = ticketService.getTickets();

    // then
    assertThat(result).singleElement().extracting(TicketResult::id).isEqualTo(1L);
  }

  @Test
  void getTickets_shouldUseUnfilteredQuery_whenFilterIsBlank() {
    // given
    when(ticketRepository.findAllOrderByCreatedAt()).thenReturn(List.of(savedTicket()));

    // when
    List<TicketResult> result = ticketService.getTickets(" ");

    // then
    assertThat(result).hasSize(1);
    verify(ticketRepository).findAllOrderByCreatedAt();
  }

  @Test
  void getTickets_shouldFilterByStatus_whenFilterIsPresent() {
    // given
    when(ticketRepository.findAllByStatusIgnoreCase("OPEN")).thenReturn(List.of(savedTicket()));

    // when
    List<TicketResult> result = ticketService.getTickets("OPEN");

    // then
    assertThat(result).hasSize(1);
    verify(ticketRepository).findAllByStatusIgnoreCase("OPEN");
  }

  @Test
  void create_shouldRejectDuplicateTitle() {
    // given
    TicketRequest request = validRequest();
    when(ticketRepository.existsByTitleIgnoreCase(request.title())).thenReturn(true);

    // when / then
    assertThatThrownBy(() -> ticketService.create(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Ya existe");
  }

  @Test
  void create_shouldRejectRequest_whenCreatorDoesNotExist() {
    // given
    TicketRequest request = validRequest();
    when(ticketRepository.existsByTitleIgnoreCase(request.title())).thenReturn(false);
    when(userRepository.findByEmail(request.createdByName())).thenReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> ticketService.create(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Usuario creador");
  }

  @Test
  void create_shouldRejectRequest_whenCreatorAndAssignedUserAreTheSame() {
    // given
    TicketRequest request = new TicketRequest(
        "Problema", "Detalle", "ana@duoc.cl", 10L, null, null, null, null);
    User creator = creator();
    when(ticketRepository.existsByTitleIgnoreCase(request.title())).thenReturn(false);
    when(userRepository.findByEmail(request.createdByName())).thenReturn(Optional.of(creator));
    when(userRepository.findById(10L)).thenReturn(Optional.of(creator));

    // when / then
    assertThatThrownBy(() -> ticketService.create(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("mismo usuario");
  }

  @Test
  void create_shouldMapAssignedUserCategoryAndTags() {
    // given
    TicketRequest request = new TicketRequest(
        "Problema", "Detalle", "ana@duoc.cl", 20L, 30L, List.of(40L), null, null);
    User assigned = new User(20L, "Luis", "luis@duoc.cl", User.Role.AGENT, true);
    Category category = new Category();
    category.setId(30L);
    category.setName("Hardware");
    Tag tag = new Tag();
    tag.setId(40L);
    tag.setName("Urgente");
    when(ticketRepository.existsByTitleIgnoreCase(request.title())).thenReturn(false);
    when(userRepository.findByEmail(request.createdByName())).thenReturn(Optional.of(creator()));
    when(userRepository.findById(20L)).thenReturn(Optional.of(assigned));
    when(categoryRepository.findById(30L)).thenReturn(Optional.of(category));
    when(tagRepository.findAllById(List.of(40L))).thenReturn(List.of(tag));
    when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
      Ticket saved = invocation.getArgument(0);
      saved.setId(1L);
      return saved;
    });

    // when
    TicketResult result = ticketService.create(request);

    // then
    assertThat(result.assignedTo().id()).isEqualTo(20L);
    assertThat(result.category().name()).isEqualTo("Hardware");
    assertThat(result.tags()).singleElement().extracting(tagResult -> tagResult.name())
        .isEqualTo("Urgente");
  }

  @Test
  void deleteById_shouldDeleteTicket_whenItExists() {
    when(ticketRepository.existsById(1L)).thenReturn(true);

    assertThat(ticketService.deleteById(1L)).isTrue();
    verify(ticketRepository).deleteById(1L);
  }

  @Test
  void deleteById_shouldReturnFalse_whenTicketDoesNotExist() {
    when(ticketRepository.existsById(99L)).thenReturn(false);

    assertThat(ticketService.deleteById(99L)).isFalse();
  }

  @Test
  void updateById_shouldReturnEmpty_whenTicketDoesNotExist() {
    when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

    assertThat(ticketService.updateById(99L, validRequest())).isEmpty();
  }

  @Test
  void updateById_shouldUpdateRelationsAndRegisterStatusChange() {
    // given
    Ticket existing = savedTicket();
    existing.setStatus("NEW");
    User assigned = new User(20L, "Luis", "luis@duoc.cl", User.Role.AGENT, true);
    Category category = new Category();
    category.setId(30L);
    category.setName("Software");
    Tag tag = new Tag();
    tag.setId(40L);
    tag.setName("Backend");
    TicketRequest request = new TicketRequest(
        "Actualizado", "Detalle nuevo", "ana@duoc.cl", 20L, 30L, List.of(40L),
        "IN_PROGRESS", LocalDateTime.of(2026, 6, 17, 12, 0));
    when(ticketRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(userRepository.findById(20L)).thenReturn(Optional.of(assigned));
    when(categoryRepository.findById(30L)).thenReturn(Optional.of(category));
    when(tagRepository.findAllById(List.of(40L))).thenReturn(List.of(tag));
    when(ticketRepository.save(existing)).thenReturn(existing);

    // when
    TicketResult result = ticketService.updateById(1L, request).orElseThrow();

    // then
    assertThat(result.status()).isEqualTo("IN_PROGRESS");
    assertThat(result.assignedTo().id()).isEqualTo(20L);
    assertThat(result.category().id()).isEqualTo(30L);
    assertThat(result.tags()).hasSize(1);
    verify(historyRepository).save(any(TicketHistory.class));
    verify(notificationClient).createNotification(any());
  }

  @Test
  void updateById_shouldRejectSameCreatorAndAssignedUser() {
    Ticket existing = savedTicket();
    TicketRequest request = new TicketRequest(
        "Actualizado", "Detalle", "ana@duoc.cl", 10L, null, null, null, null);
    when(ticketRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(userRepository.findById(10L)).thenReturn(Optional.of(creator()));

    assertThatThrownBy(() -> ticketService.updateById(1L, request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("mismo usuario");
  }

  @Test
  void getTicketHistory_shouldMapHistoryRecords() {
    TicketHistory history = new TicketHistory();
    history.setId(5L);
    history.setPreviousStatus("NEW");
    history.setNewStatus("OPEN");
    history.setChangedAt(LocalDateTime.of(2026, 6, 17, 10, 0));
    history.setComment("Estado cambiado");
    when(historyRepository.findByTicketIdOrderByChangedAtDesc(1L)).thenReturn(List.of(history));

    List<TicketHistoryResult> result = ticketService.getTicketHistory(1L);

    assertThat(result).singleElement().satisfies(item -> {
      assertThat(item.id()).isEqualTo(5L);
      assertThat(item.newStatus()).isEqualTo("OPEN");
    });
  }

  private TicketRequest validRequest() {
    return new TicketRequest(
        "Problema de acceso",
        "No puedo ingresar al sistema",
        "ana@duoc.cl",
        null,
        null,
        null,
        null,
        null
    );
  }

  private User creator() {
    return new User(10L, "Ana García", "ana@duoc.cl", User.Role.USER, true);
  }

  private Ticket savedTicket() {
    Ticket ticket = new Ticket();
    ticket.setId(1L);
    ticket.setTitle("Problema de acceso");
    ticket.setDescription("No puedo ingresar al sistema");
    ticket.setStatus("NEW");
    ticket.setCreatedBy(creator());
    return ticket;
  }
}
