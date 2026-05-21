// MaterialService.java
package pelayo.proyecto.galeiraibq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pelayo.proyecto.galeiraibq.model.Material;
import pelayo.proyecto.galeiraibq.repository.MaterialRepository;

import java.util.List;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;

    @Autowired
    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public Material addMaterial(Material material) {
        return materialRepository.save(material);
    }

    public List<Material> findAllMaterial() {
        return materialRepository.findAll();
    }

    public Material findMaterialById(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + id));
    }

    public Material updateMaterial(Material material) {
        return materialRepository.save(material);
    }

    public void deleteMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + id));
        material.setEstado_borrado(true);
        materialRepository.save(material);
    }
}