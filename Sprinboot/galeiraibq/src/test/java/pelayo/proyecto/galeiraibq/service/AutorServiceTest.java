package pelayo.proyecto.galeiraibq.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pelayo.proyecto.galeiraibq.model.Autor;
import pelayo.proyecto.galeiraibq.repository.AutorRepository;
import pelayo.proyecto.galeiraibq.responseDTO.AutorDTO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutorServiceTest {

    @Mock
    private AutorRepository autorRepository;

    @InjectMocks
    private AutorService autorService;

    // Helper para crear un autor de pruebas con todos los campos rellenos.
    // Evita repetir 7 setters en cada test.
    private Autor crearAutorEjemplo(Long id) {
        Autor a = new Autor();
        a.setId(id);
        a.setNombre("Pablo");
        a.setApellidos("Picasso");
        a.setFecha_nacimiento(1881);
        a.setFecha_muerte(1973);
        a.setCorriente_artistica("Cubismo");
        a.setLugar_nacimiento("Malaga");
        return a;
    }

    // ADD

    @Test
    void addAutor_estableceEstadoBorradoFalseYDevuelveDTOConTodosLosCampos() {
        // Arrange
        Autor entrada = crearAutorEjemplo(null);
        Autor guardado = crearAutorEjemplo(1L);
        guardado.setEstado_borrado(false);
        when(autorRepository.save(any(Autor.class))).thenReturn(guardado);

        // Act
        AutorDTO resultado = autorService.addAutor(entrada);

        // Assert: comprobamos los 7 campos del DTO uno a uno.
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Pablo", resultado.getNombre());
        assertEquals("Picasso", resultado.getApellidos());
        assertEquals(1881, resultado.getFecha_nacimiento());
        assertEquals(1973, resultado.getFecha_muerte());
        assertEquals("Cubismo", resultado.getCorriente_artistica());
        assertEquals("Malaga", resultado.getLugar_nacimiento());

        // Verificacion del comportamiento de estado_borrado=false antes del save.
        assertFalse(entrada.getEstado_borrado(),
                "addAutor debe poner estado_borrado=false antes de persistir");
    }

    // FIND ALL

    @Test
    void findAllAutor_devuelveListaDeDTOs() {
        // Arrange
        Autor a1 = crearAutorEjemplo(1L);
        Autor a2 = crearAutorEjemplo(2L);
        a2.setNombre("Joan");
        a2.setApellidos("Miro");
        when(autorRepository.findAll()).thenReturn(List.of(a1, a2));

        // Act
        List<AutorDTO> resultado = autorService.findAllAutor();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Pablo", resultado.get(0).getNombre());
        assertEquals("Miro", resultado.get(1).getApellidos());
    }

    @Test
    void findAllAutor_cuandoNoHayDatos_devuelveListaVacia() {
        when(autorRepository.findAll()).thenReturn(List.of());

        List<AutorDTO> resultado = autorService.findAllAutor();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // FIND BY ID

    @Test
    void findAutorById_cuandoExiste_devuelveDTO() {
        Autor autor = crearAutorEjemplo(5L);
        when(autorRepository.findById(5L)).thenReturn(Optional.of(autor));

        AutorDTO resultado = autorService.findAutorById(5L);

        assertEquals(5L, resultado.getId());
        assertEquals("Pablo", resultado.getNombre());
    }

    @Test
    void findAutorById_cuandoNoExiste_lanzaRuntimeException() {
        when(autorRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> autorService.findAutorById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // UPDATE

    @Test
    void updateAutor_devuelveDTO() {
        Autor autor = crearAutorEjemplo(1L);
        autor.setNombre("Pablo (modificado)");
        when(autorRepository.save(autor)).thenReturn(autor);

        AutorDTO resultado = autorService.updateAutor(autor);

        assertEquals("Pablo (modificado)", resultado.getNombre());
        verify(autorRepository).save(autor);
    }

    // DELETE

    @Test
    void deleteAutorById_marcaEstadoBorradoTrue() {
        Autor autor = crearAutorEjemplo(1L);
        autor.setEstado_borrado(false);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));

        autorService.deleteAutorById(1L);

        assertTrue(autor.getEstado_borrado(),
                "deleteAutorById debe marcar estado_borrado=true (borrado logico)");
        verify(autorRepository).save(autor);
    }

    @Test
    void deleteAutorById_cuandoNoExiste_lanzaRuntimeException() {
        when(autorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> autorService.deleteAutorById(99L));
        verify(autorRepository, never()).save(any());
    }
}
