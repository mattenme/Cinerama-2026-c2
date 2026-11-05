package cinerama.repository;

import cinerama.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByDni(String dni);

    Optional<Cliente> findByEmailAndActivo(String email, Integer activo);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);
}