// TecnicaService.java
package pelayo.proyecto.galeiraibq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pelayo.proyecto.galeiraibq.model.Tecnica;
import pelayo.proyecto.galeiraibq.repository.TecnicaRepository;
import pelayo.proyecto.galeiraibq.responseDTO.TecnicaDTO;

import java.util.List;

@Service
public class TecnicaService {
    private final TecnicaRepository tecnicaRepository;

    @Autowired
    public TecnicaService(TecnicaRepository tecnicaRepository) {
        this.tecnicaRepository = tecnicaRepository;
    }

    public TecnicaDTO addTecnica(Tecnica tecnica) {
        tecnica.setEstado_borrado(false);
        return mapToDTO(tecnicaRepository.save(tecnica));
    }

    public List<TecnicaDTO> findAllTecnica() {
        return tecnicaRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public TecnicaDTO findTecnicaById(Long id) {
        Tecnica tecnica = tecnicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tecnica no encontrada con id: " + id));
        return mapToDTO(tecnica);
    }

    public TecnicaDTO updateTecnica(Tecnica tecnica) {
        return mapToDTO(tecnicaRepository.save(tecnica));
    }

    public void deleteTecnicaById(Long id) {
        Tecnica tecnica = tecnicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tecnica no encontrada con id: " + id));
        tecnica.setEstado_borrado(true);
        tecnicaRepository.save(tecnica);
    }

    private TecnicaDTO mapToDTO(Tecnica tecnica) {
        return TecnicaDTO.builder()
                .id(tecnica.getId())
                .nombre(tecnica.getNombre())
                .build();
    }
}
