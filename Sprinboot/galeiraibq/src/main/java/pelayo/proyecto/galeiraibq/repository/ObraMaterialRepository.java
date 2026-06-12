package pelayo.proyecto.galeiraibq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pelayo.proyecto.galeiraibq.model.ObraMaterial;
import pelayo.proyecto.galeiraibq.model.compositePK.ObraMaterialId;

import java.util.List;

@Repository
public interface ObraMaterialRepository extends JpaRepository<ObraMaterial, ObraMaterialId> {
    List<ObraMaterial> findByObra_Id(Long obraId);

    // Borrado masivo por id de obra: ejecuta un DELETE directo en la BD
    // sin cargar las entidades en la sesion de Hibernate.
    @Modifying
    @Query("delete from ObraMaterial om where om.obra.id = :obraId")
    void deleteByObraId(@Param("obraId") Long obraId);
}
