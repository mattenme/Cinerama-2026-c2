package cinerama.repository;

import cinerama.entity.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    Optional<Promocion> findByCodigo(String codigo);

    Optional<Promocion> findByCodigoIgnoreCase(String codigo);

    List<Promocion> findByActivo(Integer activo);
}