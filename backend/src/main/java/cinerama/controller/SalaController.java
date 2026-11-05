package cinerama.controller;

import cinerama.entity.Sala;
import cinerama.service.ISalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
public class SalaController {

    private final ISalaService salaService;

    @GetMapping
    public ResponseEntity<List<Sala>> getAll() {
        return ResponseEntity.ok(salaService.findAll());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Sala>> getAllActive() {
        return ResponseEntity.ok(salaService.findAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sala> getById(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Sala> create(@RequestBody Sala sala) {
        sala.setIdSala(null);
        if (sala.getActivo() == null) {
            sala.setActivo(1);
        }
        if (sala.getCapacidadTotal() == null) {
            sala.setCapacidadTotal(0);
        }
        return ResponseEntity.ok(salaService.save(sala));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sala> update(@PathVariable Long id, @RequestBody Sala sala) {
        salaService.findById(id);
        sala.setIdSala(id);
        return ResponseEntity.ok(salaService.update(sala));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<Sala> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.toggleActivo(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        salaService.findById(id);
        salaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
