package cinerama.service;

import cinerama.entity.Pelicula;
import cinerama.exception.ResourceNotFoundException;
import cinerama.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PeliculaServiceImpl implements IPeliculaService {

    private final PeliculaRepository peliculaRepository;
    private final IFileStorageService fileStorageService;

    public List<Pelicula> findAll() {
        return peliculaRepository.findAll();
    }

    public List<Pelicula> findAllActive() {
        return peliculaRepository.findAllActive();
    }

    public Page<Pelicula> findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return peliculaRepository.findAll(pageable);
    }

    public Optional<Pelicula> findById(Long id) {
        return peliculaRepository.findById(id);
    }

    public List<Pelicula> findDestacadas() {
        return peliculaRepository.findByDestacadoAndActivo(1, 1);
    }

    public long count() {
        return peliculaRepository.count();
    }

    public long countActive() {
        return peliculaRepository.countByActivo(1);
    }

    @Transactional
    public Pelicula save(Pelicula pelicula) {
        return peliculaRepository.save(pelicula);
    }

    @Transactional
    public Pelicula update(Pelicula pelicula) {
        return peliculaRepository.save(pelicula);
    }

    @Transactional
    public void deleteById(Long id) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Película", id));
        String imagen = pelicula.getImagenUrl();

        peliculaRepository.deleteById(id);

        if (imagen != null && !imagen.isBlank()) {
            try {
                fileStorageService.eliminarImagen(imagen);
            } catch (IOException ignored) {
                // La película ya se borró: la imagen huérfana no debe romper la petición.
            }
        }
    }

    @Transactional
    public Pelicula toggleDestacado(Long id) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Película", id));
        pelicula.setDestacado(pelicula.getDestacado() == 1 ? 0 : 1);
        return peliculaRepository.save(pelicula);
    }

    @Transactional
    public Pelicula toggleActivo(Long id) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Película", id));
        pelicula.setActivo(pelicula.getActivo() == 1 ? 0 : 1);
        return peliculaRepository.save(pelicula);
    }

    @Transactional
    public String subirImagen(Long id, MultipartFile file) throws IOException {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Película", id));

        String imagenUrl = fileStorageService.guardarImagen(file, "peliculas");
        pelicula.setImagenUrl(imagenUrl);
        peliculaRepository.save(pelicula);

        return imagenUrl;
    }
}