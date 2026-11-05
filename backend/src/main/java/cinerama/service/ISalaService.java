package cinerama.service;

import cinerama.entity.Sala;
import java.util.List;

public interface ISalaService {
    List<Sala> findAll();
    List<Sala> findAllActive();
    Sala findById(Long id);
    Sala save(Sala sala);
    Sala update(Sala sala);
    void deleteById(Long id);
    Sala toggleActivo(Long id);
}