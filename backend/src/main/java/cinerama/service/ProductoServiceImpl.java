package cinerama.service;

import cinerama.entity.Producto;
import cinerama.exception.ResourceNotFoundException;
import cinerama.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository productoRepository;
    private final IFileStorageService fileStorageService;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public List<Producto> findAllActive() {
        return productoRepository.findByActivo(1);
    }

    public List<Producto> findByCategoria(String categoria) {
        return productoRepository.findByCategoriaAndActivo(categoria, 1);
    }

    public List<String> findAllCategorias() {
        return productoRepository.findAll().stream()
                .map(Producto::getCategoria)
                .filter(categoria -> categoria != null && !categoria.isBlank())
                .distinct()
                .toList();
    }

    public Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }

    @Transactional
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto update(Producto producto) {
        return productoRepository.save(producto);
    }

    @Transactional
    public void deleteById(Long id) {
        Producto producto = findById(id);
        String imagen = producto.getImagenUrl();

        productoRepository.deleteById(id);

        if (imagen != null && !imagen.isBlank()) {
            try {
                fileStorageService.eliminarImagen(imagen);
            } catch (IOException ignored) {
                // El producto ya se borró: la imagen huérfana no debe romper la petición.
            }
        }
    }

    @Transactional
    public Producto toggleActivo(Long id) {
        Producto producto = findById(id);
        producto.setActivo(producto.getActivo() == 1 ? 0 : 1);
        return productoRepository.save(producto);
    }

    @Transactional
    public String subirImagen(Long id, MultipartFile file) throws IOException {
        Producto producto = findById(id);
        String imagenUrl = fileStorageService.guardarImagen(file, "productos");

        String anterior = producto.getImagenUrl();
        producto.setImagenUrl(imagenUrl);
        productoRepository.save(producto);

        if (anterior != null && !anterior.isBlank()) {
            try {
                fileStorageService.eliminarImagen(anterior);
            } catch (IOException ignored) {
                // La imagen nueva ya se guardó: no se rompe la petición por no borrar la anterior.
            }
        }
        return imagenUrl;
    }
}
