package cinerama.repository;

import cinerama.entity.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    List<Pelicula> findByActivo(Integer activo);

    List<Pelicula> findByDestacadoAndActivo(Integer destacado, Integer activo);

    @Query("SELECT p FROM Pelicula p WHERE p.activo = 1 ORDER BY p.idPelicula DESC")
    List<Pelicula> findAllActive();

    long countByActivo(Integer activo);
}