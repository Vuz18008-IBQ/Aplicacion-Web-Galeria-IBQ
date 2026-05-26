package pelayo.proyecto.galeiraibq.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pelayo.proyecto.galeiraibq.model.Tecnica;
import pelayo.proyecto.galeiraibq.repository.TecnicaRepository;
import pelayo.proyecto.galeiraibq.responseDTO.TecnicaDTO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// @ExtendWith = conectar JUnit con Mockito
@ExtendWith(MockitoExtension.class)
class TecnicaServiceTest {


    @Mock
    private TecnicaRepository tecnicaRepository;


    @InjectMocks
    private TecnicaService tecnicaService;

    // ADD

    @Test
    void addTecnica_estableceEstadoBorradoFalseYDevuelveDTO() {
        // Arrange: una tecnica de entrada sin estado_borrado asignado.
        Tecnica entrada = new Tecnica();
        entrada.setNombre("Acrilico");

        // Cuando el servicio llame a save, devolvemos la misma entidad pero
        // con un id ya generado (simulando lo que MySQL haria).
        Tecnica guardada = new Tecnica();
        guardada.setId(1L);
        guardada.setNombre("Acrilico");
        guardada.setEstado_borrado(false);
        when(tecnicaRepository.save(any(Tecnica.class))).thenReturn(guardada);

        // Act
        TecnicaDTO resultado = tecnicaService.addTecnica(entrada);

        // Assert: comprobamos el DTO devuelto y que estado_borrado se fijo
        // a false ANTES del save (el patron de la implementacion).
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Acrilico", resultado.getNombre());
        assertFalse(entrada.getEstado_borrado(),
                "addTecnica debe poner estado_borrado=false antes de persistir");
        verify(tecnicaRepository, times(1)).save(entrada);
    }

    // FIND ALL

    @Test
    void findAllTecnica_devuelveListaDeDTOs() {
        // Arrange: el repositorio devuelve dos tecnicas.
        Tecnica t1 = new Tecnica();
        t1.setId(1L);
        t1.setNombre("Acrilico");
        Tecnica t2 = new Tecnica();
        t2.setId(2L);
        t2.setNombre("Oleo");
        when(tecnicaRepository.findAll()).thenReturn(List.of(t1, t2));

        // Act
        List<TecnicaDTO> resultado = tecnicaService.findAllTecnica();

        // Assert: la lista de DTOs preserva tamaño y datos.
        assertEquals(2, resultado.size());
        assertEquals("Acrilico", resultado.get(0).getNombre());
        assertEquals("Oleo", resultado.get(1).getNombre());
    }

    @Test
    void findAllTecnica_cuandoNoHayDatos_devuelveListaVacia() {
        // Arrange: la BD esta vacia.
        when(tecnicaRepository.findAll()).thenReturn(List.of());

        // Act
        List<TecnicaDTO> resultado = tecnicaService.findAllTecnica();

        // Assert: la lista no es null, simplemente vacia (caso limite tipico).
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // FIND BY ID

    @Test
    void findTecnicaById_cuandoExiste_devuelveDTO() {
        // Arrange
        Tecnica tecnica = new Tecnica();
        tecnica.setId(5L);
        tecnica.setNombre("Acuarela");
        when(tecnicaRepository.findById(5L)).thenReturn(Optional.of(tecnica));

        // Act
        TecnicaDTO resultado = tecnicaService.findTecnicaById(5L);

        // Assert
        assertEquals(5L, resultado.getId());
        assertEquals("Acuarela", resultado.getNombre());
    }

    @Test
    void findTecnicaById_cuandoNoExiste_lanzaRuntimeException() {
        // Arrange: el repositorio devuelve vacio para id=99.
        when(tecnicaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert: verificamos no solo que se lanza la excepcion sino
        // tambien que el mensaje contiene el id, util para debugging real.
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tecnicaService.findTecnicaById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // UPDATE

    @Test
    void updateTecnica_devuelveDTO() {
        // Arrange
        Tecnica tecnica = new Tecnica();
        tecnica.setId(1L);
        tecnica.setNombre("Acrilico modificado");
        when(tecnicaRepository.save(tecnica)).thenReturn(tecnica);

        // Act
        TecnicaDTO resultado = tecnicaService.updateTecnica(tecnica);

        // Assert
        assertEquals(1L, resultado.getId());
        assertEquals("Acrilico modificado", resultado.getNombre());
        verify(tecnicaRepository).save(tecnica);
    }

    //  DELETE

    @Test
    void deleteTecnicaById_marcaEstadoBorradoTrue() {
        // Arrange: la tecnica existe.
        Tecnica tecnica = new Tecnica();
        tecnica.setId(1L);
        tecnica.setNombre("Acrilico");
        tecnica.setEstado_borrado(false);
        when(tecnicaRepository.findById(1L)).thenReturn(Optional.of(tecnica));

        // Act
        tecnicaService.deleteTecnicaById(1L);

        // Assert: el borrado es LOGICO. La fila no se elimina, se marca.
        assertTrue(tecnica.getEstado_borrado(),
                "deleteTecnicaById debe marcar estado_borrado=true (borrado logico)");
        verify(tecnicaRepository).save(tecnica);
    }

    @Test
    void deleteTecnicaById_cuandoNoExiste_lanzaRuntimeException() {
        // Arrange
        when(tecnicaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class,
                () -> tecnicaService.deleteTecnicaById(99L));
        // Aseguramos de que NO se llamo a save: si la tecnica no
        // existe, no debe persistirse nada.
        verify(tecnicaRepository, never()).save(any());
    }
}
