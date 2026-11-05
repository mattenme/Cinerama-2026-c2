package cinerama.service;

import cinerama.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements IFileStorageService {

    /** Formatos que ImageIO puede leer y volver a escribir sin plugins externos. */
    private static final Set<String> EXTENSIONES_VALIDAS = Set.of("jpg", "jpeg", "png", "gif", "bmp");

    @Value("${cinerama.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public String guardarImagen(MultipartFile file, String subDirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo no puede estar vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("El archivo debe ser una imagen (JPG, PNG, GIF o BMP)");
        }

        String extension = obtenerExtension(file.getOriginalFilename(), contentType);
        if (!EXTENSIONES_VALIDAS.contains(extension)) {
            throw new BadRequestException(
                    "Formato de imagen no soportado (" + extension + "). Usa JPG, PNG, GIF o BMP");
        }

        // Siempre se guarda en un formato que ImageIO sabe escribir: PNG se mantiene,
        // el resto se convierte a JPG (así un .bmp o un .gif dejan de romper la subida).
        String formatoSalida = "png".equals(extension) ? "png" : "jpg";

        Path uploadPath = Paths.get(uploadDir, subDirectory);
        Files.createDirectories(uploadPath);

        String uniqueFilename = UUID.randomUUID().toString() + "." + formatoSalida;
        Path filePath = uploadPath.resolve(uniqueFilename);

        try (var inputStream = file.getInputStream()) {
            BufferedImage originalImage = ImageIO.read(inputStream);
            if (originalImage == null) {
                throw new BadRequestException(
                        "El archivo no es una imagen válida o su formato no está soportado");
            }

            int maxWidth = 1200;
            int maxHeight = 1200;
            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, maxWidth, maxHeight);

            boolean escrita = ImageIO.write(resizedImage, formatoSalida, filePath.toFile());
            if (!escrita) {
                Files.deleteIfExists(filePath);
                throw new BadRequestException("No se pudo procesar la imagen");
            }
        } catch (BadRequestException e) {
            Files.deleteIfExists(filePath);
            throw e;
        } catch (IOException | RuntimeException e) {
            Files.deleteIfExists(filePath);
            throw e;
        }

        return "/cinerama/uploads/" + subDirectory + "/" + uniqueFilename;
    }

    private String obtenerExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf(".") + 1)
                    .toLowerCase(Locale.ROOT);
        }
        // Sin nombre de archivo (copia desde el portapapeles): se deduce del content-type.
        String subtipo = contentType.substring("image/".length()).toLowerCase(Locale.ROOT);
        return switch (subtipo) {
            case "jpeg", "pjpeg" -> "jpg";
            case "png" -> "png";
            default -> subtipo;
        };
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