// TecnicaService.java
package pelayo.proyecto.galeiraibq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pelayo.proyecto.galeiraibq.model.Tecnica;
import pelayo.proyecto.galeiraibq.repository.TecnicaRepository;

import java.util.List;

@Service
public class TecnicaService {
    private final TecnicaRepository tecnicaRepository;

    @Autowired
    public TecnicaService(TecnicaRepository tecnicaRepository) {
        this.tecnicaRepository = tecnicaRepository;
    }

    public Tecnica addTecnica(Tecnica tecnica) {
        return tecnicaRepository.save(tecnica);
    }

    public List<Tecnica> findAllTecnica() {
        return tecnicaRepository.findAll();
    }

    public Tecnica findTecnicaById(Long id) {
        return tecnicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tecnica no encontrada con id: " + id));
    }

    public Tecnica updateTecnica(Tecnica tecnica) {
        return tecnicaRepository.save(tecnica);
    }

    public void deleteTecnicaById(Long id) {
        Tecnica tecnica = tecnicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tecnica no encontrada con id: " + id));
        tecnica.setEstado_borrado(true);
        tecnicaRepository.save(tecnica);
    }
}