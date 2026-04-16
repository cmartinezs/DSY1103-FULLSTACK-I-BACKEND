package cl.duoc.fullstack.tickets.config;

import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  private final TicketRepository ticketRepository;

  public DataInitializer(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    if (ticketRepository.count() == 0) {
      LocalDateTime now = LocalDateTime.now();
      LocalDate estimated = LocalDate.now().plusDays(5);

      Ticket t1 = new Ticket();
      t1.setTitle("Ticket 1");
      t1.setDescription("Descripción del ticket 1");
      t1.setStatus("NEW");
      t1.setCreatedAt(now);
      t1.setEstimatedResolutionDate(estimated);
      t1.setCreatedBy("admin");
      ticketRepository.save(t1);

      Ticket t2 = new Ticket();
      t2.setTitle("Ticket 2");
      t2.setDescription("Descripción del ticket 2");
      t2.setStatus("NEW");
      t2.setCreatedAt(now);
      t2.setEstimatedResolutionDate(estimated);
      t2.setCreatedBy("admin");
      ticketRepository.save(t2);
    }
  }
}
