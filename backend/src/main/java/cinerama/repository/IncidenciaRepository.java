package cinerama.repository;

import cinerama.entity.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    List<Incidencia> findByCliente_IdClienteOrderByFechaReporteDesc(Long idCliente);

    List<Incidencia> findBySala_IdSala(Long idSala);

    List<Incidencia> findByFuncion_IdFuncion(Long idFuncion);

    List<Incidencia> findByEstado(String estado);

    @Query("SELECT i FROM Incidencia i WHERE i.estado = 'PENDIENTE' ORDER BY i.fechaReporte DESC")
    List<Incidencia> findAllPending();
}