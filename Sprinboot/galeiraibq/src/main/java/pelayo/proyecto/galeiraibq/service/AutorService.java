// AutorService.java
package pelayo.proyecto.galeiraibq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pelayo.proyecto.galeiraibq.model.Autor;
import pelayo.proyecto.galeiraibq.repository.AutorRepository;

import java.util.List;

@Service
public class AutorService {
    private final AutorRepository autorRepository;

    @Autowired
    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public Autor addAutor(Autor autor) {
        return autorRepository.save(autor);
    }

    public List<Autor> findAllAutor() {
        return autorRepository.findAll();
    }

    public Autor findAutorById(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado con id: " + id));
    }

    public Autor updateAutor(Autor autor) {
        return autorRepository.save(autor);
    }

    public void deleteAutorById(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado con id: " + id));
        autor.setEstado_borrado(true);
        autorRepository.save(autor);
    }
    //BORRAR ESTE COMENTARIO. Por favor, eliminar este comentario si lo estas leyendo. NO DEBERÍA DE ESTAR AQUÍ
}