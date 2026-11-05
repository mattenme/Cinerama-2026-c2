package cinerama.service;

import cinerama.entity.Producto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductoService {
    List<Producto> findAll();
    List<Producto> findAllActive();
    List<Producto> findByCategoria(String categoria);
    List<String> findAllCategorias();
    Producto findById(Long id);
    Producto save(Producto producto);
    Producto update(Producto producto);
    void deleteById(Long id);
    Producto toggleActivo(Long id);
    String subirImagen(Long id, MultipartFile file) throws IOException;
}
