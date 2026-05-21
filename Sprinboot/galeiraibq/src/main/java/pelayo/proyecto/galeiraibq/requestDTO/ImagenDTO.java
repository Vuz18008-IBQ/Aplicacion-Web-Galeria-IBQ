package pelayo.proyecto.galeiraibq.requestDTO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ImagenDTO {
    private String url;
    private Boolean es_principal;
}
