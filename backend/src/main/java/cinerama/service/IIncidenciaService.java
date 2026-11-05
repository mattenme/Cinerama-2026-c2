package cinerama.service;

import cinerama.entity.Incidencia;
import java.util.List;

public interface IIncidenciaService {
    List<Incidencia> findAll();
    List<Incidencia> findAllPending();
    List<Incidencia> findByCliente(Long idCliente);
    List<Incidencia> findBySala(Long idSala);
    List<Incidencia> findByFuncion(Long idFuncion);
    List<Incidencia> findByEstado(String estado);
    Incidencia findById(Long id);
    Incidencia save(Incidencia incidencia);
    Incidencia update(Incidencia incidencia);
    Incidencia cambiarEstado(Long id, String estado);
    void deleteById(Long id);
}