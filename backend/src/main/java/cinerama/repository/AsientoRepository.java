package cinerama.repository;

import cinerama.entity.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {

    @Query("SELECT a FROM Asiento a JOIN FETCH a.sala WHERE a.sala.idSala = :idSala ORDER BY a.fila, a.numero")
    List<Asiento> findBySalaConRelaciones(@Param("idSala") Long idSala);

    @Query("SELECT a FROM Asiento a JOIN FETCH a.sala ORDER BY a.sala.idSala, a.fila, a.numero")
    List<Asiento> findAllConRelaciones();

    List<Asiento> findBySala_IdSalaOrderByFilaAscNumeroAsc(Long idSala);

    List<Asiento> findBySala_IdSalaAndEstado(Long idSala, String estado);

    @Query("SELECT a FROM Asiento a JOIN FETCH a.sala WHERE a.sala.idSala = :idSala AND a.estado = 'Disponible' ORDER BY a.fila, a.numero")
    List<Asiento> findAvailableBySala(@Param("idSala") Long idSala);

    @Query("SELECT COUNT(a) FROM Asiento a WHERE a.sala.idSala = :idSala AND a.estado = 'Disponible'")
    long countAvailableBySala(@Param("idSala") Long idSala);
}
