package pelayo.proyecto.galeiraibq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pelayo.proyecto.galeiraibq.model.Imagen;
import pelayo.proyecto.galeiraibq.model.Obra;
import pelayo.proyecto.galeiraibq.repository.ImagenRepository;
import pelayo.proyecto.galeiraibq.repository.ObraRepository;
import pelayo.proyecto.galeiraibq.responseDTO.ImagenResponseDTO;

@Service
public class ImagenService {
    private final ImagenRepository imagenRepository;
    private final ObraRepository obraRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public ImagenService(ImagenRepository imagenRepository, ObraRepository obraRepository, FileStorageService fileStorageService) {
        this.imagenRepository = imagenRepository;
        this.obraRepository = obraRepository;
        this.fileStorageService = fileStorageService;
    }

    //MapToDTO
    public ImagenResponseDTO mapToResponseDTO(Imagen imagen){
        return ImagenResponseDTO.builder()
                .url(imagen.getUrl())
                .es_principal(imagen.getEs_principal())
                .id(imagen.getId())
                .build();
    }

    //CRUD
    //Create
    public ImagenResponseDTO addImagen(MultipartFile file, Long obraId, Boolean esPrincipal){
        Obra obra = obraRepository.findById(obraId)
                .orElseThrow(() -> new RuntimeException("Obra no encontrada con id: " + obraId));

        String url = fileStorageService.storeFile(file, obraId);

        Imagen imagen = new Imagen();
        imagen.setEs_principal(esPrincipal != null ? esPrincipal : false);
        imagen.setUrl(url);
        imagen.setObra(obra);

        imagenRepository.save(imagen);

        return mapToResponseDTO(imagen);
    }

    //Delete
    public void deleteImagen(Long id){
        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + id));
        fileStorageService.deleteFile(imagen.getUrl());
        imagenRepository.delete(imagen);
    }
}
