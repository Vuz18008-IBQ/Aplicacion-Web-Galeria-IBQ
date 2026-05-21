package pelayo.proyecto.galeiraibq.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutorDTO {
    private Long id;
    private String nombre;
    private String apellidos;
    private Integer fecha_nacimiento;
    private Integer fecha_muerte;
    private String corriente_artistica;
    private String lugar_nacimiento;
}
