package cinerama.repository;

import cinerama.entity.Calificacion;
import cinerama.entity.CalificacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, CalificacionId> {

    List<Calificacion> findByIdPelicula(Long idPelicula);

    List<Calificacion> findByIdCliente(Long idCliente);

    @Query("SELECT AVG(c.puntuacion) FROM Calificacion c WHERE c.idPelicula = :idPelicula")
    Double findAverageByPelicula(@Param("idPelicula") Long idPelicula);

    @Query("SELECT COUNT(c) FROM Calificacion c WHERE c.idPelicula = :idPelicula")
    Long countByPelicula(@Param("idPelicula") Long idPelicula);

    Optional<Calificacion> findByIdClienteAndIdPelicula(Long idCliente, Long idPelicula);
}