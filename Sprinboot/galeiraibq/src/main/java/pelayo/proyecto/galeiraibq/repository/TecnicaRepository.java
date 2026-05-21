package pelayo.proyecto.galeiraibq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pelayo.proyecto.galeiraibq.model.Tecnica;

@Repository
public interface TecnicaRepository extends JpaRepository<Tecnica, Long> {
}