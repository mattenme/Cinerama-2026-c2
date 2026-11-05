package cinerama.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements IFileStorageService {

    @Value("${cinerama.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public String guardarImagen(MultipartFile file, String subDirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }

        Path uploadPath = Paths.get(uploadDir, subDirectory);
        Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(uniqueFilename);

        try (var inputStream = file.getInputStream()) {
            BufferedImage originalImage = ImageIO.read(inputStream);
            if (originalImage == null) {
                throw new IOException("No se pudo leer la imagen");
            }

            int maxWidth = 1200;
            int maxHeight = 1200;
            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, maxWidth, maxHeight);

            String formatName = extension.startsWith(".") ? extension.substring(1) : "jpg";
            if (formatName.equalsIgnoreCase("jpeg")) {
                formatName = "jpg";
            }

            ImageIO.write(resizedImage, formatName, filePath.toFile());
        }

        return "/cinerama/uploads/" + subDirectory + "/" + uniqueFilename;
    }

    @Override
    public void eliminarImagen(String ruta) throws IOException {
        if (ruta == null || ruta.isBlank()) {
            return;
        }
        String rutaRelativa = ruta.startsWith("/cinerama/uploads/") ? ruta.substring("/cinerama/uploads/".length()) : ruta;
        Path filePath = Paths.get(uploadDir, rutaRelativa);
        Files.deleteIfExists(filePath);
    }

    @Override
    public Optional<String> getRutaCompleta(String rutaRelativa) {
        Path filePath = Paths.get(uploadDir, rutaRelativa);
        return Files.exists(filePath) ? Optional.of(filePath.toAbsolutePath().toString()) : Optional.empty();
    }
}