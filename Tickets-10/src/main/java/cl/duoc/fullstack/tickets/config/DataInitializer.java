package cl.duoc.fullstack.tickets.config;

import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.model.User;
import cl.duoc.fullstack.tickets.model.User.Role;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import cl.duoc.fullstack.tickets.respository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  private final TicketRepository ticketRepository;
  private final UserRepository userRepository;

  public DataInitializer(TicketRepository ticketRepository, UserRepository userRepository) {
    this.ticketRepository = ticketRepository;
    this.userRepository = userRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    if (userRepository.count() == 0) {
      User admin = new User();
      admin.setName("Admin User");
      admin.setEmail("admin@tickets.com");
      admin.setRole(Role.ADMIN);
      admin.setActive(true);
      userRepository.save(admin);

      User agent = new User();
      agent.setName("Agent Smith");
      agent.setEmail("agent@tickets.com");
      agent.setRole(Role.AGENT);
      agent.setActive(true);
      userRepository.save(agent);

      User user = new User();
      user.setName("John Doe");
      user.setEmail("john@tickets.com");
      user.setRole(Role.USER);
      user.setActive(true);
      userRepository.save(user);
    }

    if (ticketRepository.count() == 0) {
      LocalDateTime now = LocalDateTime.now();
      LocalDate estimated = LocalDate.now().plusDays(5);

      User creator = userRepository.findByEmail("john@tickets.com").orElse(null);
      User assignee = userRepository.findByEmail("agent@tickets.com").orElse(null);

      if (creator != null && assignee != null) {
        Ticket t1 = new Ticket();
        t1.setTitle("Ticket 1");
        t1.setDescription("Descripción del ticket 1");
        t1.setStatus("NEW");
        t1.setCreatedAt(now);
        t1.setEstimatedResolutionDate(estimated);
        t1.setCreatedBy(creator);
        t1.setAssignedTo(assignee);
        ticketRepository.save(t1);

        Ticket t2 = new Ticket();
        t2.setTitle("Ticket 2");
        t2.setDescription("Descripción del ticket 2");
        t2.setStatus("IN_PROGRESS");
        t2.setCreatedAt(now);
        t2.setEstimatedResolutionDate(estimated);
        t2.setCreatedBy(creator);
        t2.setAssignedTo(assignee);
        ticketRepository.save(t2);
      }
    }
  }
}