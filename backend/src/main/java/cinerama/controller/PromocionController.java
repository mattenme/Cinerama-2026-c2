package cinerama.controller;

import cinerama.entity.Promocion;
import cinerama.service.IPromocionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promociones")
@RequiredArgsConstructor
public class PromocionController {

    private final IPromocionService promocionService;

    @GetMapping
    public ResponseEntity<List<Promocion>> getAll() {
        return ResponseEntity.ok(promocionService.findAll());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Promocion>> getAllActive() {
        return ResponseEntity.ok(promocionService.findAllActive());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Promocion> getByCodigo(@PathVariable String codigo) {
        return promocionService.findByCodigo(codigo.trim())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promocion> getById(@PathVariable Long id) {
        return promocionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Promocion> create(@RequestBody Promocion promocion) {
        promocion.setIdPromocion(null);
        if (promocion.getActivo() == null) {
            promocion.setActivo(1);
        }
        return ResponseEntity.ok(promocionService.save(promocion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promocion> update(@PathVariable Long id, @RequestBody Promocion promocion) {
        Promocion actual = promocionService.findById(id).orElse(null);
        if (actual == null) {
            return ResponseEntity.notFound().build();
        }
        promocion.setIdPromocion(id);
        if (promocion.getActivo() == null) {
            promocion.setActivo(actual.getActivo());
        }
        return ResponseEntity.ok(promocionService.update(promocion));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<Promocion> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(promocionService.toggleActivo(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        promocionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
