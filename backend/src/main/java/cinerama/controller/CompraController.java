package cinerama.controller;

import cinerama.dto.CompraRequest;
import cinerama.dto.CompraResponse;
import cinerama.entity.Compra;
import cinerama.service.ICompraService;
import cinerama.util.QRUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final ICompraService compraService;

    @GetMapping
    public ResponseEntity<List<CompraResponse>> getAll() {
        return ResponseEntity.ok(compraService.findAll().stream().map(CompraResponse::from).toList());
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<CompraResponse>> getByCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(
                compraService.findByCliente(idCliente).stream().map(CompraResponse::from).toList()
        );
    }

    @GetMapping("/funcion/{idFuncion}/confirmadas")
    public ResponseEntity<List<CompraResponse>> getConfirmadasByFuncion(@PathVariable Long idFuncion) {
        return ResponseEntity.ok(
                compraService.findConfirmedByFuncion(idFuncion).stream().map(CompraResponse::from).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(CompraResponse.from(compraService.findById(id)));
    }

    /** Imagen PNG del QR, generada al vuelo a partir del código corto guardado. */
    @GetMapping(value = "/{id}/qr.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQr(@PathVariable Long id) {
        Compra compra = compraService.findById(id);
        byte[] imagen = QRUtil.generarQRBytes(compra.getCodigoQr());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                .body(imagen);
    }

    @PostMapping("/procesar")
    public ResponseEntity<List<CompraResponse>> procesar(@Valid @RequestBody CompraRequest request) {
        return ResponseEntity.ok(
                compraService.procesarCompra(request).stream().map(CompraResponse::from).toList()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compraService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
