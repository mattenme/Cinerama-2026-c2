package cinerama.service;

import cinerama.entity.Promocion;
import java.util.List;
import java.util.Optional;

public interface IPromocionService {
    List<Promocion> findAll();
    List<Promocion> findAllActive();
    Optional<Promocion> findById(Long id);
    Optional<Promocion> findByCodigo(String codigo);
    Promocion save(Promocion promocion);
    Promocion update(Promocion promocion);
    void deleteById(Long id);
    Promocion toggleActivo(Long id);
}