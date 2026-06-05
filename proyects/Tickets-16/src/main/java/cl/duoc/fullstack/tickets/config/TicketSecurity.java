package cl.duoc.fullstack.tickets.config;

import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("ticketSecurity")
public class TicketSecurity {

  private final TicketRepository ticketRepository;

  public TicketSecurity(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  public boolean canEdit(Long ticketId, Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    if (hasRole(authentication, "ROLE_ADMIN")) {
      return true;
    }

    String email = authentication.getName();

    return ticketRepository.findById(ticketId)
        .map(ticket -> canEditTicket(ticket, email, authentication))
        .orElse(false);
  }

  private boolean canEditTicket(Ticket ticket, String email, Authentication authentication) {
    if (hasRole(authentication, "ROLE_USER")) {
      return ticket.getCreatedBy() != null
          && email.equals(ticket.getCreatedBy().getEmail());
    }

    if (hasRole(authentication, "ROLE_AGENT")) {
      return ticket.getAssignedTo() != null
          && email.equals(ticket.getAssignedTo().getEmail());
    }

    return false;
  }

  private boolean hasRole(Authentication authentication, String role) {
    return authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals(role));
  }
}
