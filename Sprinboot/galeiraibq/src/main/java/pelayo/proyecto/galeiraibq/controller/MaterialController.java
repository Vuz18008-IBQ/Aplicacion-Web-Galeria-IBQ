// MaterialController.java
package pelayo.proyecto.galeiraibq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pelayo.proyecto.galeiraibq.model.Material;
import pelayo.proyecto.galeiraibq.service.MaterialService;

import java.util.List;

@RestController
@RequestMapping("/api/materiales")
public class MaterialController {
    private final MaterialService materialService;

    @Autowired
    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public ResponseEntity<List<Material>> getAllMateriales() {
        return ResponseEntity.ok(materialService.findAllMaterial());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Material> getMaterialById(@PathVariable Long id) {
        return ResponseEntity.ok(materialService.findMaterialById(id));
    }

    @PostMapping
    public ResponseEntity<Material> addMaterial(@RequestBody Material material) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materialService.addMaterial(material));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Material> updateMaterial(@PathVariable Long id, @RequestBody Material material) {
        material.setId(id);
        return ResponseEntity.ok(materialService.updateMaterial(material));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterialById(id);
        return ResponseEntity.noContent().build();
    }
}