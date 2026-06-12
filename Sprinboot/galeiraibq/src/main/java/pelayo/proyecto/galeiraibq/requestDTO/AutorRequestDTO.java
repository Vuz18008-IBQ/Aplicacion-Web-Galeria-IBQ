package pelayo.proyecto.galeiraibq.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutorRequestDTO {
    private String nombre;
    private String apellidos;
    private Integer fecha_nacimiento;
    private Integer fecha_muerte;
    private String corriente_artistica;
    private String lugar_nacimiento;
}
