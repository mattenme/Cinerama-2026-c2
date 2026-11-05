package cinerama.service;

import cinerama.entity.Cliente;
import java.util.List;
import java.util.Optional;

public interface IClienteService {
    List<Cliente> findAll();
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
    Cliente save(Cliente cliente);
    Cliente update(Cliente cliente);
    void deleteById(Long id);
    Cliente toggleActivo(Long id);
    Cliente verificarEmail(Long id);
}