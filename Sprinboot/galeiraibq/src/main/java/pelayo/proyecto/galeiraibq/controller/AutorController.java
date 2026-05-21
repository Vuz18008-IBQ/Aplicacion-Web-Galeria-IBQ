// AutorController.java
package pelayo.proyecto.galeiraibq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pelayo.proyecto.galeiraibq.model.Autor;
import pelayo.proyecto.galeiraibq.service.AutorService;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
public class AutorController {
    private final AutorService autorService;

    @Autowired
    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping
    public ResponseEntity<List<Autor>> getAllAutores() {
        return ResponseEntity.ok(autorService.findAllAutor());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> getAutorById(@PathVariable Long id) {
        return ResponseEntity.ok(autorService.findAutorById(id));
    }

    @PostMapping
    public ResponseEntity<Autor> addAutor(@RequestBody Autor autor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.addAutor(autor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Autor> updateAutor(@PathVariable Long id, @RequestBody Autor autor) {
        autor.setId(id);
        return ResponseEntity.ok(autorService.updateAutor(autor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAutor(@PathVariable Long id) {
        autorService.deleteAutorById(id);
        return ResponseEntity.noContent().build();
    }
}