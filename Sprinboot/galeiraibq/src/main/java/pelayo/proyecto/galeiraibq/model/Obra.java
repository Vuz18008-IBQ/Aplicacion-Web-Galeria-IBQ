package pelayo.proyecto.galeiraibq.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;


@Entity
@SQLRestriction("estado_borrado = false")
@Data
@Table(name = "obra")
public class Obra implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String datacion;
    private Integer anio;
    private String dimensiones;
    private String tipologia;
    private String descripcion;
    private String marcas_inscripciones;
    private String referencias;
    private Date fecha_ingreso;
    private String modo_ingreso;
    private String procedencia;
    private String estado_conservacion;
    private String restauraciones;
    private String ubicacion;
    private String observaciones;
    private Boolean estado_borrado;

    @OneToMany(mappedBy = "obra")
    private List<Imagen> imagenes;
    @OneToMany(mappedBy = "obra")
    private List<ObraMaterial> obraMateriales;

    @ManyToOne
    @JoinColumn(name = "tecnica_id")
    private Tecnica tecnica;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;


}
