// MaterialService.java
package pelayo.proyecto.galeiraibq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pelayo.proyecto.galeiraibq.model.Material;
import pelayo.proyecto.galeiraibq.repository.MaterialRepository;
import pelayo.proyecto.galeiraibq.requestDTO.MaterialRequestDTO;
import pelayo.proyecto.galeiraibq.responseDTO.MaterialResponseDTO;

import java.util.List;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;

    @Autowired
    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public MaterialResponseDTO addMaterial(MaterialRequestDTO dto) {
        Material material = new Material();
        material.setNombre(dto.getNombre());
        material.setEstado_borrado(false);
        return mapToDTO(materialRepository.save(material));
    }

    public List<MaterialResponseDTO> findAllMaterial() {
        return materialRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public MaterialResponseDTO findMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + id));
        return mapToDTO(material);
    }

    public MaterialResponseDTO updateMaterial(MaterialRequestDTO dto, Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + id));
        material.setNombre(dto.getNombre());
        material.setEstado_borrado(false);
        return mapToDTO(materialRepository.save(material));
    }

    public void deleteMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con id: " + id));
        material.setEstado_borrado(true);
        materialRepository.save(material);
    }

    private MaterialResponseDTO mapToDTO(Material material) {
        return MaterialResponseDTO.builder()
                .id(material.getId())
                .nombre(material.getNombre())
                .build();
    }
}
