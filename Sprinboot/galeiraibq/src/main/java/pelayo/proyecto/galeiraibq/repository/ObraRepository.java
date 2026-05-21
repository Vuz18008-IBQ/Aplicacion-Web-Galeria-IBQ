package pelayo.proyecto.galeiraibq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pelayo.proyecto.galeiraibq.model.Obra;

@Repository
public interface ObraRepository extends JpaRepository<Obra, Long> {
}
