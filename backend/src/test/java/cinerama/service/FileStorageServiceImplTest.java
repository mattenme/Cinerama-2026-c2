package cinerama.service;

import cinerama.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceImplTest {

    private FileStorageServiceImpl service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new FileStorageServiceImpl();
        ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());
    }

    private byte[] imagenBytes(String formato) throws IOException {
        BufferedImage imagen = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        ImageIO.write(imagen, formato, salida);
        return salida.toByteArray();
    }

    @Test
    void guardaPngConservandoElFormato() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "poster.png", "image/png", imagenBytes("png"));

        String url = service.guardarImagen(file, "peliculas");

        assertThat(url).startsWith("/cinerama/uploads/peliculas/").endsWith(".png");
        Path guardado = tempDir.resolve("peliculas").resolve(url.substring(url.lastIndexOf('/') + 1));
        assertThat(Files.exists(guardado)).isTrue();
    }

    @Test
    void convierteGifAJpgEnLugarDeRomper() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "poster.gif", "image/gif", imagenBytes("gif"));

        String url = service.guardarImagen(file, "peliculas");

        assertThat(url).endsWith(".jpg");
    }

    @Test
    void rechazaWebpConErrorDePeticion() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "poster.webp", "image/webp", imagenBytes("png"));

        assertThatThrownBy(() -> service.guardarImagen(file, "peliculas"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("no soportado");

        Path carpeta = tempDir.resolve("peliculas");
        boolean hayArchivos = false;
        if (Files.exists(carpeta)) {
            try (var archivos = Files.list(carpeta)) {
                hayArchivos = archivos.findAny().isPresent();
            }
        }
        assertThat(hayArchivos).isFalse();
    }

    @Test
    void rechazaArchivoQueNoEsImagen() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "documento.pdf", "application/pdf", new byte[]{1, 2, 3});

        assertThatThrownBy(() -> service.guardarImagen(file, "peliculas"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("debe ser una imagen");
    }

    @Test
    void rechazaImagenConContenidoCorrupto() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "rota.jpg", "image/jpeg", new byte[]{1, 2, 3});

        assertThatThrownBy(() -> service.guardarImagen(file, "peliculas"))
                .isInstanceOf(BadRequestException.class);
    }
}
