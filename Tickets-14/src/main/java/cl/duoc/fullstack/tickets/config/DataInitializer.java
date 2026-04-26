package cl.duoc.fullstack.tickets.config;

import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.model.User;
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
    if (ticketRepository.count() == 0) {
      LocalDateTime now = LocalDateTime.now();
      LocalDate estimated = LocalDate.now().plusDays(5);

      User ana = new User();
      ana.setName("Ana Garcia");
      ana.setEmail("ana.garcia@empresa.com");
      userRepository.save(ana);

      User carlos = new User();
      carlos.setName("Carlos Lopez");
      carlos.setEmail("carlos.lopez@empresa.com");
      userRepository.save(carlos);

      Ticket t1 = new Ticket();
      t1.setTitle("Error en login");
      t1.setDescription("No se puede iniciar sesion con Google");
      t1.setStatus("NEW");
      t1.setCreatedAt(now);
      t1.setEstimatedResolutionDate(estimated);
      t1.setCreatedBy(ana);
      ticketRepository.save(t1);

      Ticket t2 = new Ticket();
      t2.setTitle("Mejora en dashboard");
      t2.setDescription("Agregar graficos de estadisticas");
      t2.setStatus("IN_PROGRESS");
      t2.setCreatedAt(now);
      t2.setEstimatedResolutionDate(estimated);
      t2.setCreatedBy(carlos);
      ticketRepository.save(t2);

      Ticket t3 = new Ticket();
      t3.setTitle("Documentacion API");
      t3.setDescription("Falta documentacion de endpoints");
      t3.setStatus("NEW");
      t3.setCreatedAt(now);
      t3.setEstimatedResolutionDate(estimated);
      t3.setCreatedBy(ana);
      ticketRepository.save(t3);
    }
  }
}
