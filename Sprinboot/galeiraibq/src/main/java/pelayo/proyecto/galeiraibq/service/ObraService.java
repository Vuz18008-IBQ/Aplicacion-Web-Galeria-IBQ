package pelayo.proyecto.galeiraibq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pelayo.proyecto.galeiraibq.model.Autor;
import pelayo.proyecto.galeiraibq.model.Imagen;
import pelayo.proyecto.galeiraibq.model.Material;
import pelayo.proyecto.galeiraibq.model.Obra;
import pelayo.proyecto.galeiraibq.model.ObraMaterial;
import pelayo.proyecto.galeiraibq.model.Tecnica;
import pelayo.proyecto.galeiraibq.repository.AutorRepository;
import pelayo.proyecto.galeiraibq.repository.MaterialRepository;
import pelayo.proyecto.galeiraibq.repository.ObraMaterialRepository;
import pelayo.proyecto.galeiraibq.repository.ObraRepository;
import pelayo.proyecto.galeiraibq.repository.TecnicaRepository;
import pelayo.proyecto.galeiraibq.requestDTO.ObraRequestDTO;
import pelayo.proyecto.galeiraibq.responseDTO.AutorDTO;
import pelayo.proyecto.galeiraibq.responseDTO.ImagenResponseDTO;
import pelayo.proyecto.galeiraibq.responseDTO.MaterialResponseDTO;
import pelayo.proyecto.galeiraibq.responseDTO.ObraResponseDTO;
import pelayo.proyecto.galeiraibq.responseDTO.TecnicaDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class ObraService {
    private final ObraRepository obraRepository;
    private final AutorRepository autorRepository;
    private final TecnicaRepository tecnicaRepository;
    private final MaterialRepository materialRepository;
    private final ObraMaterialRepository obraMaterialRepository;

    @Autowired
    public ObraService(ObraRepository obraRepository, AutorRepository autorRepository, TecnicaRepository tecnicaRepository, MaterialRepository materialRepository, ObraMaterialRepository obraMaterialRepository) {
        this.obraRepository = obraRepository;
        this.autorRepository = autorRepository;
        this.tecnicaRepository = tecnicaRepository;
        this.materialRepository = materialRepository;
        this.obraMaterialRepository = obraMaterialRepository;
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

    //MapToMaterialDTO
    public MaterialResponseDTO mapToMaterialDTO(Material material) {
        return MaterialResponseDTO.builder()
                .id(material.getId())
                .nombre(material.getNombre())
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
                .materiales(obra.getObraMateriales() != null
                        ? obra.getObraMateriales().stream()
                            .map(ObraMaterial::getMaterial)
                            .filter(m -> m != null)
                            .map(this::mapToMaterialDTO)
                            .toList()
                        : null)
                .imagenes(obra.getImagenes() != null
                        ? obra.getImagenes().stream().map(this::mapToImagenDTO).toList()
                        : null)
                .id(obra.getId())
                .build();
    }

    //CRUD Operations
    @Transactional
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
        syncMateriales(obra, dto.getMaterialIds());


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

    @Transactional
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
        syncMateriales(obra, dto.getMaterialIds());


        return mapToRequestDTO(obra);
    }

    //Sincroniza la tabla obra_material con los materialIds recibidos
    private void syncMateriales(Obra obra, List<Long> materialIds) {
        obraMaterialRepository.deleteByObraId(obra.getId());

        List<ObraMaterial> nuevos = new ArrayList<>();
        if (materialIds != null) {
            for (Long materialId : materialIds) {
                Material material = materialRepository.findById(materialId)
                        .orElseThrow(() -> new RuntimeException("Material no encontrado: " + materialId));
                ObraMaterial obraMaterial = new ObraMaterial();
                obraMaterial.setObra(obra);
                obraMaterial.setMaterial(material);
                obraMaterialRepository.save(obraMaterial);
                nuevos.add(obraMaterial);
            }
        }
        obra.setObraMateriales(nuevos);
    }

    public void deleteObraById(Long id) {
        Obra obra = obraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obra no encontrada con id: " + id));
        obra.setEstado_borrado(true);
        obraRepository.save(obra);
    }
}
