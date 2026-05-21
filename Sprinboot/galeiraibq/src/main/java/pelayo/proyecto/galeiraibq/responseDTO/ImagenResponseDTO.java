package pelayo.proyecto.galeiraibq.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagenResponseDTO {
    private Long id;
    private String url;
    private Boolean es_principal;
}
