package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
  boolean existsByTitleIgnoreCase(String title);

  @Query("SELECT t FROM Ticket t WHERE UPPER(t.status) = UPPER(:status) ORDER BY t.createdAt")
  List<Ticket> findAllByStatusIgnoreCase(@Param("status") String status);

  @Query("SELECT t FROM Ticket t ORDER BY t.createdAt")
  List<Ticket> findAllOrderByCreatedAt();
}
