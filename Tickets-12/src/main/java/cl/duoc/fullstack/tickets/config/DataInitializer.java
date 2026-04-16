package cl.duoc.fullstack.tickets.config;

import cl.duoc.fullstack.tickets.model.Category;
import cl.duoc.fullstack.tickets.model.Tag;
import cl.duoc.fullstack.tickets.model.Ticket;
import cl.duoc.fullstack.tickets.respository.CategoryRepository;
import cl.duoc.fullstack.tickets.respository.TagRepository;
import cl.duoc.fullstack.tickets.respository.TicketRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  private final TicketRepository ticketRepository;
  private final CategoryRepository categoryRepository;
  private final TagRepository tagRepository;

  public DataInitializer(
      TicketRepository ticketRepository,
      CategoryRepository categoryRepository,
      TagRepository tagRepository) {
    this.ticketRepository = ticketRepository;
    this.categoryRepository = categoryRepository;
    this.tagRepository = tagRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    if (ticketRepository.count() == 0) {
      LocalDateTime now = LocalDateTime.now();
      LocalDate estimated = LocalDate.now().plusDays(5);

      Category catBug = new Category();
      catBug.setName("Bug");
      catBug.setDescription("Issues y problemas");
      Category savedBugCat = categoryRepository.save(catBug);

      Category catFeature = new Category();
      catFeature.setName("Feature");
      catFeature.setDescription("Nuevas funcionalidades");
      Category savedFeatureCat = categoryRepository.save(catFeature);

      Tag tagUrgent = new Tag();
      tagUrgent.setName("Urgent");
      tagUrgent.setColor("#FF0000");
      Tag savedUrgentTag = tagRepository.save(tagUrgent);

      Tag tagBackend = new Tag();
      tagBackend.setName("Backend");
      tagBackend.setColor("#0000FF");
      Tag savedBackendTag = tagRepository.save(tagBackend);

      Tag tagUI = new Tag();
      tagUI.setName("UI");
      tagUI.setColor("#00FF00");
      Tag savedUITag = tagRepository.save(tagUI);

      Ticket t1 = new Ticket();
      t1.setTitle("Ticket 1");
      t1.setDescription("Descripción del ticket 1");
      t1.setStatus("NEW");
      t1.setCreatedAt(now);
      t1.setEstimatedResolutionDate(estimated);
      t1.setCreatedBy("admin");
      t1.setCategory(savedBugCat);
      t1.setTags(List.of(savedUrgentTag, savedBackendTag));
      ticketRepository.save(t1);

      Ticket t2 = new Ticket();
      t2.setTitle("Ticket 2");
      t2.setDescription("Descripción del ticket 2");
      t2.setStatus("NEW");
      t2.setCreatedAt(now);
      t2.setEstimatedResolutionDate(estimated);
      t2.setCreatedBy("admin");
      t2.setCategory(savedFeatureCat);
      t2.setTags(List.of(savedUITag));
      ticketRepository.save(t2);
    }
  }
}
