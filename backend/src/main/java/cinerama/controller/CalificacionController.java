package cinerama.controller;

import cinerama.entity.Calificacion;
import cinerama.service.ICalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
public class CalificacionController {

    private final ICalificacionService calificacionService;

    @GetMapping
    public ResponseEntity<List<Calificacion>> getAll() {
        return ResponseEntity.ok(calificacionService.findAll());
    }

    @GetMapping("/pelicula/{idPelicula}")
    public ResponseEntity<List<Calificacion>> getByPelicula(@PathVariable Long idPelicula) {
        return ResponseEntity.ok(calificacionService.findByPelicula(idPelicula));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Calificacion>> getByCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(calificacionService.findByCliente(idCliente));
    }

    @GetMapping("/pelicula/{idPelicula}/promedio")
    public ResponseEntity<Map<String, Object>> getPromedio(@PathVariable Long idPelicula) {
        Double promedio = calificacionService.getPromedioByPelicula(idPelicula);
        Long total = calificacionService.getCountByPelicula(idPelicula);
        return ResponseEntity.ok(Map.of(
                "idPelicula", idPelicula,
                "promedio", promedio == null ? 0 : promedio,
                "total", total == null ? 0 : total
        ));
    }

    @GetMapping("/{idPelicula}/{idCliente}")
    public ResponseEntity<Calificacion> getById(
            @PathVariable Long idPelicula,
            @PathVariable Long idCliente) {
        return calificacionService.findByClienteAndPelicula(idCliente, idPelicula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Calificacion> create(@RequestBody Calificacion calificacion) {
        return ResponseEntity.ok(calificacionService.upsert(calificacion));
    }

    @DeleteMapping("/{idPelicula}/{idCliente}")
    public ResponseEntity<Void> delete(
            @PathVariable Long idPelicula,
            @PathVariable Long idCliente) {
        calificacionService.deleteById(idCliente, idPelicula);
        return ResponseEntity.noContent().build();
    }
}
