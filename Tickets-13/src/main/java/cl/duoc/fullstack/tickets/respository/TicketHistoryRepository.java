package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.TicketHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {

  List<TicketHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId);
}