package cinerama.controller;

import cinerama.entity.Producto;
import cinerama.service.IProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final IProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> getAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Producto>> getAllActive() {
        return ResponseEntity.ok(productoService.findAllActive());
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> getCategorias() {
        return ResponseEntity.ok(productoService.findAllCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Producto> create(@RequestBody Producto producto) {
        producto.setIdProducto(null);
        if (producto.getActivo() == null) {
            producto.setActivo(1);
        }
        return ResponseEntity.ok(productoService.save(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(@PathVariable Long id, @RequestBody Producto producto) {
        Producto actual = productoService.findById(id);
        producto.setIdProducto(id);
        if (producto.getImagenUrl() == null) {
            producto.setImagenUrl(actual.getImagenUrl());
        }
        if (producto.getActivo() == null) {
            producto.setActivo(actual.getActivo());
        }
        return ResponseEntity.ok(productoService.update(producto));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<Producto> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.toggleActivo(id));
    }

    @PostMapping("/{id}/imagen")
    public ResponseEntity<Map<String, String>> subirImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        String imagenUrl = productoService.subirImagen(id, file);
        return ResponseEntity.ok(Map.of("imagenUrl", imagenUrl));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
