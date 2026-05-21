package pelayo.proyecto.galeiraibq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pelayo.proyecto.galeiraibq.model.Autor;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
}