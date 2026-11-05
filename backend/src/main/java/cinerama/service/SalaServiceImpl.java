package cinerama.service;

import cinerama.entity.Sala;
import cinerama.exception.ResourceNotFoundException;
import cinerama.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalaServiceImpl implements ISalaService {

    private final SalaRepository salaRepository;

    public List<Sala> findAll() {
        return salaRepository.findAll();
    }

    public List<Sala> findAllActive() {
        return salaRepository.findByActivoOrderByNombreAsc(1);
    }

    public Sala findById(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala", id));
    }

    @Transactional
    public Sala save(Sala sala) {
        return salaRepository.save(sala);
    }

    @Transactional
    public Sala update(Sala sala) {
        return salaRepository.save(sala);
    }

    @Transactional
    public void deleteById(Long id) {
        salaRepository.deleteById(id);
    }

    @Transactional
    public Sala toggleActivo(Long id) {
        Sala sala = findById(id);
        sala.setActivo(sala.getActivo() == 1 ? 0 : 1);
        return salaRepository.save(sala);
    }
}