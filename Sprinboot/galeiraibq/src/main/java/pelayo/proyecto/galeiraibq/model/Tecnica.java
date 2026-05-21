package pelayo.proyecto.galeiraibq.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigInteger;
import java.util.List;

@Entity
@SQLRestriction("estado_borrado = false")
@Data
@Table(name = "tecnica")
public class Tecnica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Boolean estado_borrado;

    @OneToMany(mappedBy = "tecnica")
    private List<Obra> obras;
    @OneToMany(mappedBy = "tecnica")
    private List<AutorTecnica> autorTecnicas;

}
