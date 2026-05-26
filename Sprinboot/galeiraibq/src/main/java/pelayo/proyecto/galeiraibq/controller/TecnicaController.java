// TecnicaController.java
package pelayo.proyecto.galeiraibq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pelayo.proyecto.galeiraibq.model.Tecnica;
import pelayo.proyecto.galeiraibq.responseDTO.TecnicaDTO;
import pelayo.proyecto.galeiraibq.service.TecnicaService;

import java.util.List;

@RestController
@RequestMapping("/api/tecnicas")
public class TecnicaController {
    private final TecnicaService tecnicaService;

    @Autowired
    public TecnicaController(TecnicaService tecnicaService) {
        this.tecnicaService = tecnicaService;
    }

    @GetMapping
    public ResponseEntity<List<TecnicaDTO>> getAllTecnicas() {
        return ResponseEntity.ok(tecnicaService.findAllTecnica());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TecnicaDTO> getTecnicaById(@PathVariable Long id) {
        return ResponseEntity.ok(tecnicaService.findTecnicaById(id));
    }

    @PostMapping
    public ResponseEntity<TecnicaDTO> addTecnica(@RequestBody Tecnica tecnica) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tecnicaService.addTecnica(tecnica));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TecnicaDTO> updateTecnica(@PathVariable Long id, @RequestBody Tecnica tecnica) {
        tecnica.setId(id);
        return ResponseEntity.ok(tecnicaService.updateTecnica(tecnica));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTecnica(@PathVariable Long id) {
        tecnicaService.deleteTecnicaById(id);
        return ResponseEntity.noContent().build();
    }
}
