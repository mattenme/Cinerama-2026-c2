package cinerama.service;

import cinerama.entity.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IPeliculaService {
    List<Pelicula> findAll();
    List<Pelicula> findAllActive();
    Page<Pelicula> findAllPaginated(int page, int size);
    Optional<Pelicula> findById(Long id);
    List<Pelicula> findDestacadas();
    long count();
    long countActive();
    Pelicula save(Pelicula pelicula);
    Pelicula update(Pelicula pelicula);
    void deleteById(Long id);
    Pelicula toggleDestacado(Long id);
    Pelicula toggleActivo(Long id);
    String subirImagen(Long id, MultipartFile file) throws IOException;
}