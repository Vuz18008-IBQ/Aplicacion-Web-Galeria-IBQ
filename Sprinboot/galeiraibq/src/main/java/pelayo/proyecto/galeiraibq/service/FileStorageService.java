package pelayo.proyecto.galeiraibq.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/webp"
    );

    public String storeFile(MultipartFile file, Long obraId) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo esta vacio");
        }
        if (!TIPOS_PERMITIDOS.contains(file.getContentType())) {
            throw new RuntimeException("Tipo de archivo no permitido: " + file.getContentType());
        }

        String extension = obtenerExtension(file.getContentType());
        String nombreFichero = UUID.randomUUID() + extension;

        Path carpetaObra = Paths.get(uploadDir, "obras", obraId.toString());
        try {
            Files.createDirectories(carpetaObra);
            Path destino = carpetaObra.resolve(nombreFichero);
            file.transferTo(destino.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Error guardando el archivo: " + e.getMessage());
        }

        return "/uploads/obras/" + obraId + "/" + nombreFichero;
    }

    public void deleteFile(String url) {
        if (url == null || url.isBlank()) return;
        try {
            Path ruta = Paths.get(uploadDir).resolve(url.replaceFirst("^/uploads/", ""));
            Files.deleteIfExists(ruta);
        } catch (IOException e) {
            throw new RuntimeException("Error borrando el archivo: " + e.getMessage());
        }
    }

    private String obtenerExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
