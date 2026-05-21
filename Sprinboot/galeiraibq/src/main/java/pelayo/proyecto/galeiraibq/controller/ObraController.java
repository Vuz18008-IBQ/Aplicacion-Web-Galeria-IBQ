package pelayo.proyecto.galeiraibq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pelayo.proyecto.galeiraibq.model.Obra;
import pelayo.proyecto.galeiraibq.requestDTO.ObraRequestDTO;
import pelayo.proyecto.galeiraibq.responseDTO.ObraResponseDTO;
import pelayo.proyecto.galeiraibq.service.ObraService;

import java.util.List;

@RestController
@RequestMapping("/api/obras")
public class ObraController {
    private final ObraService obraService;

    @Autowired
    public ObraController(ObraService obraService) {
        this.obraService = obraService;
    }

    @GetMapping
    public ResponseEntity<List<ObraResponseDTO>> getAllObras() {
        return ResponseEntity.ok(obraService.findAllObra());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObraResponseDTO> getObraById(@PathVariable Long id) {
        return ResponseEntity.ok(obraService.findObraById(id));
    }

    @PostMapping
    public ResponseEntity<ObraResponseDTO> addObra(@RequestBody ObraRequestDTO obra) {
        return ResponseEntity.status(HttpStatus.CREATED).body(obraService.addObra(obra));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObraResponseDTO> updateObra(@PathVariable Long id, @RequestBody ObraRequestDTO obra) {
        return ResponseEntity.ok(obraService.updateObra(obra, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObra(@PathVariable Long id) {
        obraService.deleteObraById(id);
        return ResponseEntity.noContent().build();
    }
}
