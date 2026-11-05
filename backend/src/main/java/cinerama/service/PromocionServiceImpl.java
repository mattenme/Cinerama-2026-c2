package cinerama.service;

import cinerama.entity.Promocion;
import cinerama.repository.PromocionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromocionServiceImpl implements IPromocionService {

    private final PromocionRepository promocionRepository;

    public List<Promocion> findAll() {
        return promocionRepository.findAll();
    }

    public List<Promocion> findAllActive() {
        return promocionRepository.findByActivo(1);
    }

    public Optional<Promocion> findById(Long id) {
        return promocionRepository.findById(id);
    }

    public Optional<Promocion> findByCodigo(String codigo) {
        return promocionRepository.findByCodigoIgnoreCase(codigo.trim());
    }

    @Transactional
    public Promocion save(Promocion promocion) {
        return promocionRepository.save(promocion);
    }

    @Transactional
    public Promocion update(Promocion promocion) {
        return promocionRepository.save(promocion);
    }

    @Transactional
    public void deleteById(Long id) {
        promocionRepository.deleteById(id);
    }

    @Transactional
    public Promocion toggleActivo(Long id) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promocion no encontrada: " + id));
        promocion.setActivo(promocion.getActivo() == 1 ? 0 : 1);
        return promocionRepository.save(promocion);
    }
}