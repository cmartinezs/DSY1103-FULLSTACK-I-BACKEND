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
      t1.setTitle("Error en login");
      t1.setDescription("No se puede iniciar sesión con Google");
      t1.setStatus("NEW");
      t1.setCreatedAt(now);
      t1.setEstimatedResolutionDate(estimated);
      ticketRepository.save(t1);

      Ticket t2 = new Ticket();
      t2.setTitle("Mejora en dashboard");
      t2.setDescription("Agregar gráficos de estadísticas");
      t2.setStatus("IN_PROGRESS");
      t2.setCreatedAt(now);
      t2.setEstimatedResolutionDate(estimated);
      ticketRepository.save(t2);

      Ticket t3 = new Ticket();
      t3.setTitle("Documentación API");
      t3.setDescription("Falta文档ación de endpoints");
      t3.setStatus("NEW");
      t3.setCreatedAt(now);
      t3.setEstimatedResolutionDate(estimated);
      ticketRepository.save(t3);
    }
  }
}