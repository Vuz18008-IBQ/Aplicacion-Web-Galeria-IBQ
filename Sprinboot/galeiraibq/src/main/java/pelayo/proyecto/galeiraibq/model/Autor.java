package pelayo.proyecto.galeiraibq.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.util.List;

@Entity
@SQLRestriction("estado_borrado = false")
@Data
@Table(name = "autor")
public class Autor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellidos;
    private Integer fecha_nacimiento;
    private Integer fecha_muerte;
    private String corriente_artistica;
    private String lugar_nacimiento;
    private Boolean estado_borrado;

    @OneToMany(mappedBy = "autor")
    private List<AutorTecnica> autorTecnicas;
}
