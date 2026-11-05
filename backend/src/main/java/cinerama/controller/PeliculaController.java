package cinerama.controller;

import cinerama.entity.Pelicula;
import cinerama.service.IPeliculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/peliculas")
@RequiredArgsConstructor
public class PeliculaController {

    private final IPeliculaService peliculaService;

    @GetMapping
    public ResponseEntity<List<Pelicula>> getAll() {
        return ResponseEntity.ok(peliculaService.findAll());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Pelicula>> getAllActive() {
        return ResponseEntity.ok(peliculaService.findAllActive());
    }

    @GetMapping("/destacadas")
    public ResponseEntity<List<Pelicula>> getFeatured() {
        return ResponseEntity.ok(peliculaService.findDestacadas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> getById(@PathVariable Long id) {
        return peliculaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pelicula> create(@RequestBody Pelicula pelicula) {
        pelicula.setIdPelicula(null);
        normalizar(pelicula);
        return ResponseEntity.ok(peliculaService.save(pelicula));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelicula> update(@PathVariable Long id, @RequestBody Pelicula pelicula) {
        Pelicula actual = peliculaService.findById(id).orElse(null);
        if (actual == null) {
            return ResponseEntity.notFound().build();
        }

        pelicula.setIdPelicula(id);
        normalizar(pelicula);
        // Si el formulario no envía la imagen, se conserva la que ya tenía.
        if (pelicula.getImagenUrl() == null) {
            pelicula.setImagenUrl(actual.getImagenUrl());
        }
        return ResponseEntity.ok(peliculaService.update(pelicula));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<Pelicula> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.toggleActivo(id));
    }

    @PatchMapping("/{id}/destacado")
    public ResponseEntity<Pelicula> toggleDestacado(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.toggleDestacado(id));
    }

    @PostMapping("/{id}/imagen")
    public ResponseEntity<Map<String, String>> subirImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        String imagenUrl = peliculaService.subirImagen(id, file);
        return ResponseEntity.ok(Map.of("imagenUrl", imagenUrl));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        peliculaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void normalizar(Pelicula pelicula) {
        if (pelicula.getActivo() == null) {
            pelicula.setActivo(1);
        }
        if (pelicula.getDestacado() == null) {
            pelicula.setDestacado(0);
        }
    }
}
