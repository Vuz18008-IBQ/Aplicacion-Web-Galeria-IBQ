package pelayo.proyecto.galeiraibq.model;

import jakarta.persistence.*;
import lombok.Data;
import pelayo.proyecto.galeiraibq.model.compositePK.ObraMaterialId;

import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Data
@IdClass(ObraMaterialId.class)
@Table(name = "obra_material")
public class ObraMaterial implements Serializable {
    @Id
    private Long id_obra;
    @Id
    private Long id_material;

    @ManyToOne
    @MapsId("id_obra")
    @JoinColumn(name = "id_obra")
    private Obra obra;

    @ManyToOne
    @MapsId("id_material")
    @JoinColumn(name = "id_material")
    private Material material;
}
