package pelayo.proyecto.galeiraibq.model;

import jakarta.persistence.*;
import lombok.Data;
import pelayo.proyecto.galeiraibq.model.compositePK.AutorTecnicaId;

import java.io.Serializable;

@Entity
@Data
@Table(name = "autor_tecnica")
@IdClass(AutorTecnicaId.class)
public class AutorTecnica implements Serializable {
    @Id
    private Long id_autor;
    @Id
    private Long id_tecnica;

    @ManyToOne
    @JoinColumn(name = "id_autor")
    @MapsId("id_autor")
    private Autor autor;
    @ManyToOne
    @JoinColumn(name = "id_tecnica")
    @MapsId("id_tecnica")
    private Tecnica tecnica;
}
