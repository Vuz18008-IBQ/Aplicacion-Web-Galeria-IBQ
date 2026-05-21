package pelayo.proyecto.galeiraibq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pelayo.proyecto.galeiraibq.model.Autor;
import pelayo.proyecto.galeiraibq.model.Imagen;
import pelayo.proyecto.galeiraibq.model.Obra;
import pelayo.proyecto.galeiraibq.model.Tecnica;
import pelayo.proyecto.galeiraibq.repository.AutorRepository;
import pelayo.proyecto.galeiraibq.repository.ObraRepository;
import pelayo.proyecto.galeiraibq.repository.TecnicaRepository;
import pelayo.proyecto.galeiraibq.requestDTO.ObraRequestDTO;
import pelayo.proyecto.galeiraibq.responseDTO.AutorDTO;
import pelayo.proyecto.galeiraibq.responseDTO.ImagenResponseDTO;
import pelayo.proyecto.galeiraibq.responseDTO.ObraResponseDTO;
import pelayo.proyecto.galeiraibq.responseDTO.TecnicaDTO;

import java.util.List;

@Service
public class ObraService {
    private final ObraRepository obraRepository;
    private final AutorRepository autorRepository;
    private final TecnicaRepository tecnicaRepository;

    @Autowired
    public ObraService(ObraRepository obraRepository, AutorRepository autorRepository, TecnicaRepository tecnicaRepository) {
        this.obraRepository = obraRepository;
        this.autorRepository = autorRepository;
        this.tecnicaRepository = tecnicaRepository;
    }


    //MapToAutorDTO
    public AutorDTO mapToAutorDTO(Autor autor) {
        return AutorDTO.builder()
                .nombre(autor.getNombre())
                .apellidos(autor.getApellidos())
                .corriente_artistica(autor.getCorriente_artistica())
                .fecha_muerte(autor.getFecha_muerte())
                .fecha_nacimiento(autor.getFecha_nacimiento())
                .id(autor.getId())
                .lugar_nacimiento(autor.getLugar_nacimiento())
                .build();
    }

    //MapToTecnicaDTO
    public TecnicaDTO mapToTecnicaDTO(Tecnica tecnica) {
        return TecnicaDTO.builder()
                .id(tecnica.getId())
                .nombre(tecnica.getNombre())
                .build();
    }

    //MapToImagenDTO
    public ImagenResponseDTO mapToImagenDTO(Imagen imagen) {
        return ImagenResponseDTO.builder()
                .id(imagen.getId())
                .url(imagen.getUrl())
                .es_principal(imagen.getEs_principal())
                .build();
    }

    //MapToObraResponseDTO
    public ObraResponseDTO mapToRequestDTO(Obra obra) {
        return ObraResponseDTO.builder()
                .datacion(obra.getDatacion())
                .anio(obra.getAnio())
                .descripcion(obra.getDescripcion())
                .dimensiones(obra.getDimensiones())
                .estado_conservacion(obra.getEstado_conservacion())
                .fecha_ingreso(obra.getFecha_ingreso())
                .marcas_inscripciones(obra.getMarcas_inscripciones())
                .modo_ingreso(obra.getModo_ingreso())
                .observaciones(obra.getObservaciones())
                .procedencia(obra.getProcedencia())
                .referencias(obra.getReferencias())
                .restauraciones(obra.getRestauraciones())
                .tipologia(obra.getTipologia())
                .titulo(obra.getTitulo())
                .ubicacion(obra.getUbicacion())
                .autor(obra.getAutor() != null ? mapToAutorDTO(obra.getAutor()) : null)
                .tecnica(obra.getTecnica() != null ? mapToTecnicaDTO(obra.getTecnica()) : null)
                .imagenes(obra.getImagenes() != null
                        ? obra.getImagenes().stream().map(this::mapToImagenDTO).toList()
                        : null)
                .id(obra.getId())
                .build();
    }

    //CRUD Operations
    public ObraResponseDTO addObra(ObraRequestDTO dto) {
        Obra obra = new Obra();

        if (dto.getAutorId() != null) {
            obra.setAutor(autorRepository.findById(dto.getAutorId())
                    .orElseThrow(() -> new RuntimeException("Autor no encontrado: " + dto.getAutorId())));
        }
        if (dto.getTecnicaId() != null) {
            obra.setTecnica(tecnicaRepository.findById(dto.getTecnicaId())
                    .orElseThrow(() -> new RuntimeException("Tecnica no encontrada: " + dto.getTecnicaId())));
        }
        obra.setTitulo(dto.getTitulo());
        obra.setDimensiones(dto.getDimensiones());
        obra.setTipologia(dto.getTipologia());
        obra.setDescripcion(dto.getDescripcion());
        obra.setMarcas_inscripciones(dto.getMarcas_inscripciones());
        obra.setReferencias(dto.getReferencias());
        obra.setFecha_ingreso(dto.getFecha_ingreso());
        obra.setModo_ingreso(dto.getModo_ingreso());
        obra.setProcedencia(dto.getProcedencia());
        obra.setEstado_conservacion(dto.getEstado_conservacion());
        obra.setRestauraciones(dto.getRestauraciones());
        obra.setUbicacion(dto.getUbicacion());
        obra.setObservaciones(dto.getObservaciones());
        obra.setEstado_borrado(false);
        obra.setDatacion(dto.getDatacion());
        obra.setAnio(dto.getAnio());

        obraRepository.save(obra);


        return mapToRequestDTO(obra);
    }

    public List<ObraResponseDTO> findAllObra() {
        return obraRepository.findAll().stream().map(this::mapToRequestDTO).toList();
    }

    public ObraResponseDTO findObraById(Long id) {
        Obra obra = obraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obra no encontrada con id: " + id));
        return mapToRequestDTO(obra);
    }

    public ObraResponseDTO updateObra(ObraRequestDTO dto, Long id) {
        Obra obra = obraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obra no encontrada con id: " + id));

        if (dto.getAutorId() != null) {
            obra.setAutor(autorRepository.findById(dto.getAutorId())
                    .orElseThrow(() -> new RuntimeException("Autor no encontrado: " + dto.getAutorId())));
        }
        if (dto.getTecnicaId() != null) {
            obra.setTecnica(tecnicaRepository.findById(dto.getTecnicaId())
                    .orElseThrow(() -> new RuntimeException("Tecnica no encontrada: " + dto.getTecnicaId())));
        }

        obra.setTitulo(dto.getTitulo());
        obra.setDimensiones(dto.getDimensiones());
        obra.setTipologia(dto.getTipologia());
        obra.setDescripcion(dto.getDescripcion());
        obra.setMarcas_inscripciones(dto.getMarcas_inscripciones());
        obra.setReferencias(dto.getReferencias());
        obra.setFecha_ingreso(dto.getFecha_ingreso());
        obra.setModo_ingreso(dto.getModo_ingreso());
        obra.setProcedencia(dto.getProcedencia());
        obra.setEstado_conservacion(dto.getEstado_conservacion());
        obra.setRestauraciones(dto.getRestauraciones());
        obra.setUbicacion(dto.getUbicacion());
        obra.setObservaciones(dto.getObservaciones());
        obra.setEstado_borrado(false);
        obra.setDatacion(dto.getDatacion());
        obra.setAnio(dto.getAnio());

        obraRepository.save(obra);


        return mapToRequestDTO(obra);
    }

    public void deleteObraById(Long id) {
        Obra obra = obraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obra no encontrada con id: " + id));
        obra.setEstado_borrado(true);
        obraRepository.save(obra);
    }
}
