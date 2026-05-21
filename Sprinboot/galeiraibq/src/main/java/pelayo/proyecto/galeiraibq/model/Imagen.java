package pelayo.proyecto.galeiraibq.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Data
@Table(name = "imagen")
public class Imagen implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    private Boolean es_principal;

    @ManyToOne
    @JoinColumn(name = "id_obra")
    private Obra obra;
}
