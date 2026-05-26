// AutorService.java
package pelayo.proyecto.galeiraibq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pelayo.proyecto.galeiraibq.model.Autor;
import pelayo.proyecto.galeiraibq.repository.AutorRepository;
import pelayo.proyecto.galeiraibq.responseDTO.AutorDTO;

import java.util.List;

@Service
public class AutorService {
    private final AutorRepository autorRepository;

    @Autowired
    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public AutorDTO addAutor(Autor autor) {
        autor.setEstado_borrado(false);
        return mapToDTO(autorRepository.save(autor));
    }

    public List<AutorDTO> findAllAutor() {
        return autorRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public AutorDTO findAutorById(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado con id: " + id));
        return mapToDTO(autor);
    }

    public AutorDTO updateAutor(Autor autor) {
        return mapToDTO(autorRepository.save(autor));
    }

    public void deleteAutorById(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado con id: " + id));
        autor.setEstado_borrado(true);
        autorRepository.save(autor);
    }

    private AutorDTO mapToDTO(Autor autor) {
        return AutorDTO.builder()
                .id(autor.getId())
                .nombre(autor.getNombre())
                .apellidos(autor.getApellidos())
                .fecha_nacimiento(autor.getFecha_nacimiento())
                .fecha_muerte(autor.getFecha_muerte())
                .corriente_artistica(autor.getCorriente_artistica())
                .lugar_nacimiento(autor.getLugar_nacimiento())
                .build();
    }
}
