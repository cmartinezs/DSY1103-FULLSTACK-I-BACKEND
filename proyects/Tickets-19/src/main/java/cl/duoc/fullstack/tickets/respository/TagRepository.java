package cl.duoc.fullstack.tickets.respository;

import cl.duoc.fullstack.tickets.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
  boolean existsByNameIgnoreCase(String name);
}
