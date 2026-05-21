package pelayo.proyecto.galeiraibq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pelayo.proyecto.galeiraibq.model.Imagen;
import pelayo.proyecto.galeiraibq.model.Obra;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findByObra(Obra obra);
}
