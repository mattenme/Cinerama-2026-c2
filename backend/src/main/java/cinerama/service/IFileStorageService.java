package cinerama.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface IFileStorageService {
    String guardarImagen(MultipartFile file, String subDirectory) throws IOException;
    void eliminarImagen(String ruta) throws IOException;
    Optional<String> getRutaCompleta(String rutaRelativa);
}