package cinerama.service;

import cinerama.entity.Calificacion;
import java.util.List;
import java.util.Optional;

public interface ICalificacionService {
    List<Calificacion> findAll();
    List<Calificacion> findByPelicula(Long idPelicula);
    List<Calificacion> findByCliente(Long idCliente);
    Optional<Calificacion> findByClienteAndPelicula(Long idCliente, Long idPelicula);
    Double getPromedioByPelicula(Long idPelicula);
    Long getCountByPelicula(Long idPelicula);
    Calificacion save(Calificacion calificacion);
    Calificacion update(Calificacion calificacion);
    void deleteById(Long idCliente, Long idPelicula);
    Calificacion upsert(Calificacion calificacion);
}