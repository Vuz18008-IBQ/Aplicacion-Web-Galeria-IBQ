package pelayo.proyecto.galeiraibq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pelayo.proyecto.galeiraibq.responseDTO.ImagenResponseDTO;
import pelayo.proyecto.galeiraibq.service.ImagenService;

@RestController
@RequestMapping("/api")
public class ImagenController {
    private final ImagenService imagenService;

    @Autowired
    public ImagenController(ImagenService imagenService) {
        this.imagenService = imagenService;
    }

    @PostMapping("/obras/{obraId}/imagenes")
    public ResponseEntity<ImagenResponseDTO> addImagen(
            @PathVariable Long obraId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "esPrincipal", required = false) Boolean esPrincipal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imagenService.addImagen(file, obraId, esPrincipal));
    }

    @DeleteMapping("/imagenes/{id}")
    public ResponseEntity<Void> deleteImagen(@PathVariable Long id) {
        imagenService.deleteImagen(id);
        return ResponseEntity.noContent().build();
    }
}
