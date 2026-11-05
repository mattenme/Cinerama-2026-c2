package cinerama.service;

import cinerama.entity.Asiento;
import cinerama.exception.ResourceNotFoundException;
import cinerama.repository.AsientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsientoServiceImpl implements IAsientoService {

    private final AsientoRepository asientoRepository;

    public List<Asiento> findAll() {
        return asientoRepository.findAllConRelaciones();
    }

    public List<Asiento> findBySala(Long idSala) {
        return asientoRepository.findBySalaConRelaciones(idSala);
    }

    public List<Asiento> findAvailableBySala(Long idSala) {
        return asientoRepository.findAvailableBySala(idSala);
    }

    public long countAvailableBySala(Long idSala) {
        return asientoRepository.countAvailableBySala(idSala);
    }

    public Asiento findById(Long id) {
        return asientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asiento", id));
    }

    @Transactional
    public Asiento save(Asiento asiento) {
        return asientoRepository.save(asiento);
    }

    @Transactional
    public List<Asiento> saveAll(List<Asiento> asientos) {
        return asientoRepository.saveAll(asientos);
    }

    @Transactional
    public Asiento updateEstado(Long id, String estado) {
        Asiento asiento = findById(id);
        asiento.setEstado(estado);
        return asientoRepository.save(asiento);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!asientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Asiento", id);
        }
        asientoRepository.deleteById(id);
    }

    @Transactional
    public void deleteBySala(Long idSala) {
        asientoRepository.deleteAll(asientoRepository.findBySala_IdSalaOrderByFilaAscNumeroAsc(idSala));
    }
}