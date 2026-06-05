package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.TicketHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {

  // Devuelve el historial de un ticket ordenado del más reciente al más antiguo
  List<TicketHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId);
}
