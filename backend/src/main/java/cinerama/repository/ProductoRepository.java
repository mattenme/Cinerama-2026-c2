package cinerama.repository;

import cinerama.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivo(Integer activo);

    List<Producto> findByCategoriaAndActivo(String categoria, Integer activo);

    List<Producto> findByCategoriaOrderByNombreAsc(String categoria);
}