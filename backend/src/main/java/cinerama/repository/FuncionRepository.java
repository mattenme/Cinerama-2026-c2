package cinerama.repository;

import cinerama.entity.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {

    List<Funcion> findByPelicula_IdPeliculaAndActivo(Long idPelicula, Integer activo);

    List<Funcion> findBySala_IdSalaAndActivo(Long idSala, Integer activo);

    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula JOIN FETCH f.sala ORDER BY f.horaInicio")
    List<Funcion> findAllWithRelations();

    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula JOIN FETCH f.sala WHERE f.activo = 1 ORDER BY f.horaInicio")
    List<Funcion> findAllActive();

    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula JOIN FETCH f.sala WHERE f.pelicula.idPelicula = :idPelicula AND f.activo = 1 ORDER BY f.horaInicio")
    List<Funcion> findByPeliculaIdActive(@Param("idPelicula") Long idPelicula);

    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula JOIN FETCH f.sala WHERE f.sala.idSala = :idSala AND f.activo = 1 ORDER BY f.horaInicio")
    List<Funcion> findBySalaIdActiveWithRelations(@Param("idSala") Long idSala);

    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula JOIN FETCH f.sala WHERE f.idFuncion = :id")
    java.util.Optional<Funcion> findByIdWithRelations(@Param("id") Long id);

    /** Funciones activas de una sala que podrían solaparse con un horario dado. */
    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula WHERE f.sala.idSala = :idSala "
            + "AND f.activo = 1 AND f.horaInicio BETWEEN :desde AND :hasta")
    List<Funcion> findActivasEnRango(@Param("idSala") Long idSala,
                                     @Param("desde") LocalDateTime desde,
                                     @Param("hasta") LocalDateTime hasta);
}