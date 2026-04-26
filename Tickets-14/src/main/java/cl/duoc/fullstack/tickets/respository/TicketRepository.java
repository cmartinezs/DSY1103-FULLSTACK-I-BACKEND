package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

  boolean existsByTitleIgnoreCase(String title);

  List<Ticket> findByStatusIgnoreCase(String status);

  List<Ticket> findAllByOrderByCreatedAtAsc();
}
