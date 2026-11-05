package cinerama.controller;

import cinerama.dto.FuncionRequest;
import cinerama.dto.FuncionResponse;
import cinerama.service.IFuncionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
public class FuncionController {

    private final IFuncionService funcionService;

    @GetMapping
    public ResponseEntity<List<FuncionResponse>> getAll() {
        return ResponseEntity.ok(funcionService.findAll().stream().map(FuncionResponse::from).toList());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<FuncionResponse>> getAllActive() {
        return ResponseEntity.ok(funcionService.findAllActive().stream().map(FuncionResponse::from).toList());
    }

    @GetMapping("/pelicula/{idPelicula}")
    public ResponseEntity<List<FuncionResponse>> getByPelicula(@PathVariable Long idPelicula) {
        return ResponseEntity.ok(
                funcionService.findByPelicula(idPelicula).stream().map(FuncionResponse::from).toList()
        );
    }

    @GetMapping("/sala/{idSala}")
    public ResponseEntity<List<FuncionResponse>> getBySala(@PathVariable Long idSala) {
        return ResponseEntity.ok(
                funcionService.findBySala(idSala).stream().map(FuncionResponse::from).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(FuncionResponse.from(funcionService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<FuncionResponse> create(@Valid @RequestBody FuncionRequest request) {
        return ResponseEntity.ok(FuncionResponse.from(funcionService.save(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionResponse> update(@PathVariable Long id, @Valid @RequestBody FuncionRequest request) {
        return ResponseEntity.ok(FuncionResponse.from(funcionService.update(id, request)));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<FuncionResponse> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(FuncionResponse.from(funcionService.toggleActivo(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        funcionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
