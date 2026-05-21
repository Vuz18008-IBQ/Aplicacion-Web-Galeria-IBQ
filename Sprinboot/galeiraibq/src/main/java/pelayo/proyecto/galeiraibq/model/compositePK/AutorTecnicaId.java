package pelayo.proyecto.galeiraibq.model.compositePK;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutorTecnicaId implements Serializable {
    private Long id_autor;
    private Long id_tecnica;
}
