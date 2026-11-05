package cinerama.service;

import cinerama.dto.CompraRequest;
import cinerama.entity.Compra;

import java.util.List;
import java.util.Optional;

public interface ICompraService {
    List<Compra> findAll();
    List<Compra> findByCliente(Long idCliente);
    List<Compra> findByFuncion(Long idFuncion);
    List<Compra> findConfirmedByFuncion(Long idFuncion);
    Optional<Compra> findByCodigoQr(String codigoQr);
    Compra findById(Long id);
    Compra save(Compra compra);
    Compra update(Compra compra);
    void deleteById(Long id);
    boolean isAsientoOcupado(Long idFuncion, Long idAsiento);
    List<Compra> procesarCompra(CompraRequest request);
}
