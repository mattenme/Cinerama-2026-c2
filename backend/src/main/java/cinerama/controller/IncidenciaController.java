package cinerama.controller;

import cinerama.entity.Incidencia;
import cinerama.service.IIncidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IIncidenciaService incidenciaService;

    @GetMapping
    public ResponseEntity<List<Incidencia>> getAll() {
        return ResponseEntity.ok(incidenciaService.findAll());
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Incidencia>> getPendientes() {
        return ResponseEntity.ok(incidenciaService.findAllPending());
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Incidencia>> getByCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(incidenciaService.findByCliente(idCliente));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Incidencia>> getByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(incidenciaService.findByEstado(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incidencia> getById(@PathVariable Long id) {
        return ResponseEntity.ok(incidenciaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Incidencia> create(@RequestBody Incidencia incidencia) {
        incidencia.setIdIncidencia(null);
        return ResponseEntity.ok(incidenciaService.save(incidencia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incidencia> update(@PathVariable Long id, @RequestBody Incidencia incidencia) {
        incidenciaService.findById(id);
        incidencia.setIdIncidencia(id);
        return ResponseEntity.ok(incidenciaService.update(incidencia));
    }

    @PatchMapping("/{id}/estado/{estado}")
    public ResponseEntity<Incidencia> cambiarEstado(@PathVariable Long id, @PathVariable String estado) {
        return ResponseEntity.ok(incidenciaService.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        incidenciaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
