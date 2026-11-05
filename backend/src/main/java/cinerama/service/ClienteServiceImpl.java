package cinerama.service;

import cinerama.entity.Cliente;
import cinerama.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteServiceImpl implements IClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> findByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public Optional<Cliente> findByDni(String dni) {
        return clienteRepository.findByDni(dni);
    }

    public boolean existsByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    public boolean existsByDni(String dni) {
        return clienteRepository.existsByDni(dni);
    }

    @Transactional
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente update(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }

    @Transactional
    public Cliente toggleActivo(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + id));
        cliente.setActivo(cliente.getActivo() == 1 ? 0 : 1);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente verificarEmail(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + id));
        cliente.setVerificado(1);
        cliente.setCodigoVerificacion(null);
        return clienteRepository.save(cliente);
    }
}