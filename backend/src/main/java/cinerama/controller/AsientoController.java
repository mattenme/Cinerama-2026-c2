package cinerama.controller;

import cinerama.dto.AsientoResponse;
import cinerama.entity.Asiento;
import cinerama.service.IAsientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asientos")
@RequiredArgsConstructor
public class AsientoController {

    private final IAsientoService asientoService;

    @GetMapping
    public ResponseEntity<List<AsientoResponse>> getAll() {
        return ResponseEntity.ok(asientoService.findAll().stream().map(AsientoResponse::from).toList());
    }

    @GetMapping("/sala/{idSala}")
    public ResponseEntity<List<AsientoResponse>> getBySala(@PathVariable Long idSala) {
        return ResponseEntity.ok(
                asientoService.findBySala(idSala).stream().map(AsientoResponse::from).toList()
        );
    }

    @GetMapping("/sala/{idSala}/disponibles")
    public ResponseEntity<List<AsientoResponse>> getAvailableBySala(@PathVariable Long idSala) {
        return ResponseEntity.ok(
                asientoService.findAvailableBySala(idSala).stream().map(AsientoResponse::from).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsientoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(AsientoResponse.from(asientoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<AsientoResponse> create(@RequestBody Asiento asiento) {
        return ResponseEntity.ok(AsientoResponse.from(asientoService.save(asiento)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        asientoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
