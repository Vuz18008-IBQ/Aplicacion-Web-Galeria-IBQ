package pelayo.proyecto.galeiraibq.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObraResponseDTO {
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
    private AutorDTO autor;
    private TecnicaDTO tecnica;
    private List<MaterialResponseDTO> materiales;
    private List<ImagenResponseDTO> imagenes;
}
