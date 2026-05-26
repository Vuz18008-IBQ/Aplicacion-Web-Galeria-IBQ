package pelayo.proyecto.galeiraibq.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pelayo.proyecto.galeiraibq.model.Autor;
import pelayo.proyecto.galeiraibq.model.Imagen;
import pelayo.proyecto.galeiraibq.model.Obra;
import pelayo.proyecto.galeiraibq.model.Tecnica;
import pelayo.proyecto.galeiraibq.repository.AutorRepository;
import pelayo.proyecto.galeiraibq.repository.ObraRepository;
import pelayo.proyecto.galeiraibq.repository.TecnicaRepository;
import pelayo.proyecto.galeiraibq.requestDTO.ObraRequestDTO;
import pelayo.proyecto.galeiraibq.responseDTO.ObraResponseDTO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObraServiceTest {

    // Tres mocks debido a que el servicio depende de tres repositorios distintos.
    @Mock private ObraRepository obraRepository;
    @Mock private AutorRepository autorRepository;
    @Mock private TecnicaRepository tecnicaRepository;

    @InjectMocks
    private ObraService obraService;

    // Helpers de construccion

    private Autor autorEjemplo(Long id) {
        Autor a = new Autor();
        a.setId(id);
        a.setNombre("Pablo");
        a.setApellidos("Picasso");
        return a;
    }

    private Tecnica tecnicaEjemplo(Long id) {
        Tecnica t = new Tecnica();
        t.setId(id);
        t.setNombre("Oleo");
        return t;
    }

    private ObraRequestDTO requestEjemplo(Long autorId, Long tecnicaId) {
        return ObraRequestDTO.builder()
                .titulo("Guernica")
                .datacion("1937")
                .anio(1937)
                .dimensiones("349 x 776 cm")
                .descripcion("Obra cumbre del cubismo")
                .autorId(autorId)
                .tecnicaId(tecnicaId)
                .build();
    }

    private Obra obraEjemplo(Long id) {
        Obra o = new Obra();
        o.setId(id);
        o.setTitulo("Guernica");
        o.setDatacion("1937");
        o.setAnio(1937);
        o.setAutor(autorEjemplo(1L));
        o.setTecnica(tecnicaEjemplo(1L));
        o.setEstado_borrado(false);
        return o;
    }

    // ADD

    @Test
    void addObra_conIdsValidos_devuelveDTOConDatosCompletos() {
        // Arrange: el servicio buscara autor y tecnica por id.
        ObraRequestDTO dto = requestEjemplo(1L, 1L);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autorEjemplo(1L)));
        when(tecnicaRepository.findById(1L)).thenReturn(Optional.of(tecnicaEjemplo(1L)));

        // thenAnswer permite que el mock devuelva exactamente el objeto que
        // se le paso como argumento.
        when(obraRepository.save(any(Obra.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ObraResponseDTO resultado = obraService.addObra(dto);

        // Assert: comprobamos el mapeo completo: campos planos, autor anidado,
        // tecnica anidada.
        assertNotNull(resultado);
        assertEquals("Guernica", resultado.getTitulo());
        assertEquals(1937, resultado.getAnio());
        assertEquals("1937", resultado.getDatacion());
        assertNotNull(resultado.getAutor());
        assertEquals("Pablo", resultado.getAutor().getNombre());
        assertNotNull(resultado.getTecnica());
        assertEquals("Oleo", resultado.getTecnica().getNombre());
        verify(obraRepository).save(any(Obra.class));
    }

    @Test
    void addObra_cuandoAutorIdNoExiste_lanzaRuntimeException() {
        ObraRequestDTO dto = requestEjemplo(99L, 1L);
        when(autorRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> obraService.addObra(dto));
        assertTrue(ex.getMessage().contains("Autor"));
        assertTrue(ex.getMessage().contains("99"));

        // Importante: si el autor no existe, NO debe llamarse al save.
        verify(obraRepository, never()).save(any());
    }

    @Test
    void addObra_cuandoTecnicaIdNoExiste_lanzaRuntimeException() {
        ObraRequestDTO dto = requestEjemplo(1L, 99L);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autorEjemplo(1L)));
        when(tecnicaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> obraService.addObra(dto));
        assertTrue(ex.getMessage().contains("Tecnica"));
        assertTrue(ex.getMessage().contains("99"));
        verify(obraRepository, never()).save(any());
    }

    @Test
    void addObra_conAutorIdYTecnicaIdNull_creaObraSinRelaciones() {
        // Caso limite: el dto no trae autor ni tecnica. El servicio debe
        // poder crear la obra sin consultar esos repositorios y dejar los
        // campos autor/tecnica del DTO a null.
        ObraRequestDTO dto = requestEjemplo(null, null);
        when(obraRepository.save(any(Obra.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ObraResponseDTO resultado = obraService.addObra(dto);

        assertNotNull(resultado);
        assertEquals("Guernica", resultado.getTitulo());
        assertNull(resultado.getAutor(), "Sin autorId, el autor del DTO debe ser null");
        assertNull(resultado.getTecnica(), "Sin tecnicaId, la tecnica del DTO debe ser null");
        // Y verificamos que el servicio NO consultó los repositorios:
        verify(autorRepository, never()).findById(anyLong());
        verify(tecnicaRepository, never()).findById(anyLong());
    }

    // FIND ALL

    @Test
    void findAllObra_devuelveListaDeDTOs() {
        Obra o1 = obraEjemplo(1L);
        Obra o2 = obraEjemplo(2L);
        o2.setTitulo("Las meninas");
        when(obraRepository.findAll()).thenReturn(List.of(o1, o2));

        List<ObraResponseDTO> resultado = obraService.findAllObra();

        assertEquals(2, resultado.size());
        assertEquals("Guernica", resultado.get(0).getTitulo());
        assertEquals("Las meninas", resultado.get(1).getTitulo());
    }

    // FIND BY ID

    @Test
    void findObraById_cuandoExiste_devuelveDTO() {
        Obra obra = obraEjemplo(5L);
        when(obraRepository.findById(5L)).thenReturn(Optional.of(obra));

        ObraResponseDTO resultado = obraService.findObraById(5L);

        assertEquals(5L, resultado.getId());
        assertEquals("Guernica", resultado.getTitulo());
    }

    @Test
    void findObraById_cuandoNoExiste_lanzaRuntimeException() {
        when(obraRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> obraService.findObraById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void findObraById_conImagenes_mapeaImagenesEnElDTO() {
        // Test especifico del mapeo de imagenes. IMPORTANTE!
        Obra obra = obraEjemplo(1L);
        Imagen img = new Imagen();
        img.setId(10L);
        img.setUrl("/uploads/obras/1/abc.jpg");
        img.setEs_principal(true);
        obra.setImagenes(List.of(img));
        when(obraRepository.findById(1L)).thenReturn(Optional.of(obra));

        ObraResponseDTO resultado = obraService.findObraById(1L);

        assertNotNull(resultado.getImagenes());
        assertEquals(1, resultado.getImagenes().size());
        assertEquals(10L, resultado.getImagenes().get(0).getId());
        assertEquals("/uploads/obras/1/abc.jpg", resultado.getImagenes().get(0).getUrl());
        assertTrue(resultado.getImagenes().get(0).getEs_principal());
    }

    // UPDATE

    @Test
    void updateObra_cuandoExiste_devuelveDTO() {
        ObraRequestDTO dto = requestEjemplo(1L, 1L);
        dto.setTitulo("Guernica (restaurada)");
        Obra obraExistente = obraEjemplo(1L);
        when(obraRepository.findById(1L)).thenReturn(Optional.of(obraExistente));
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autorEjemplo(1L)));
        when(tecnicaRepository.findById(1L)).thenReturn(Optional.of(tecnicaEjemplo(1L)));
        when(obraRepository.save(any(Obra.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ObraResponseDTO resultado = obraService.updateObra(dto, 1L);

        assertEquals("Guernica (restaurada)", resultado.getTitulo());
        verify(obraRepository).save(obraExistente);
    }

    @Test
    void updateObra_cuandoObraNoExiste_lanzaRuntimeException() {
        when(obraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> obraService.updateObra(requestEjemplo(1L, 1L), 99L));
        verify(obraRepository, never()).save(any());
    }

    // DELETE

    @Test
    void deleteObraById_marcaEstadoBorradoTrue() {
        Obra obra = obraEjemplo(1L);
        obra.setEstado_borrado(false);
        when(obraRepository.findById(1L)).thenReturn(Optional.of(obra));

        obraService.deleteObraById(1L);

        assertTrue(obra.getEstado_borrado(),
                "deleteObraById debe marcar estado_borrado=true (borrado logico)");
        verify(obraRepository).save(obra);
    }

    @Test
    void deleteObraById_cuandoNoExiste_lanzaRuntimeException() {
        when(obraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> obraService.deleteObraById(99L));
        verify(obraRepository, never()).save(any());
    }
}
