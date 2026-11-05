package cinerama.service;

import cinerama.dto.FuncionRequest;
import cinerama.entity.Funcion;

import java.util.List;

public interface IFuncionService {
    List<Funcion> findAll();
    List<Funcion> findAllActive();
    List<Funcion> findByPelicula(Long idPelicula);
    List<Funcion> findBySala(Long idSala);
    Funcion findById(Long id);
    Funcion save(FuncionRequest request);
    Funcion update(Long id, FuncionRequest request);
    void deleteById(Long id);
    Funcion toggleActivo(Long id);
}
