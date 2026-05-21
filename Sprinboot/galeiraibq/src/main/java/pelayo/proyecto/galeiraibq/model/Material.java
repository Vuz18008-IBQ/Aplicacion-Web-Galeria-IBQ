package pelayo.proyecto.galeiraibq.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Entity
@SQLRestriction("estado_borrado = false")
@Data
@Table(name = "material")
public class Material implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Boolean estado_borrado;

    @OneToMany(mappedBy = "material")
    private List<ObraMaterial> obraMateriales;
}
