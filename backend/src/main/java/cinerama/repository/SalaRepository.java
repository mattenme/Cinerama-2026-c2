package cinerama.repository;

import cinerama.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {

    List<Sala> findByActivo(Integer activo);

    List<Sala> findByActivoOrderByNombreAsc(Integer activo);
}