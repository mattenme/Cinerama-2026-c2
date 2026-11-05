package cinerama.service;

import cinerama.entity.Asiento;
import java.util.List;

public interface IAsientoService {
    List<Asiento> findAll();
    List<Asiento> findBySala(Long idSala);
    List<Asiento> findAvailableBySala(Long idSala);
    long countAvailableBySala(Long idSala);
    Asiento findById(Long id);
    Asiento save(Asiento asiento);
    List<Asiento> saveAll(List<Asiento> asientos);
    Asiento updateEstado(Long id, String estado);
    void deleteById(Long id);
    void deleteBySala(Long idSala);
}