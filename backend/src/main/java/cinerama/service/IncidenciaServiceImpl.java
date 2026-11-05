package cinerama.service;

import cinerama.entity.Incidencia;
import cinerama.repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncidenciaServiceImpl implements IIncidenciaService {

    private final IncidenciaRepository incidenciaRepository;

    public List<Incidencia> findAll() {
        return incidenciaRepository.findAll();
    }

    public List<Incidencia> findAllPending() {
        return incidenciaRepository.findAllPending();
    }

    public List<Incidencia> findByCliente(Long idCliente) {
        return incidenciaRepository.findByCliente_IdClienteOrderByFechaReporteDesc(idCliente);
    }

    public List<Incidencia> findBySala(Long idSala) {
        return incidenciaRepository.findBySala_IdSala(idSala);
    }

    public List<Incidencia> findByFuncion(Long idFuncion) {
        return incidenciaRepository.findByFuncion_IdFuncion(idFuncion);
    }

    public List<Incidencia> findByEstado(String estado) {
        return incidenciaRepository.findByEstado(estado);
    }

    public Incidencia findById(Long id) {
        return incidenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada: " + id));
    }

    @Transactional
    public Incidencia save(Incidencia incidencia) {
        return incidenciaRepository.save(incidencia);
    }

    @Transactional
    public Incidencia update(Incidencia incidencia) {
        return incidenciaRepository.save(incidencia);
    }

    @Transactional
    public Incidencia cambiarEstado(Long id, String estado) {
        Incidencia incidencia = findById(id);
        incidencia.setEstado(estado);
        return incidenciaRepository.save(incidencia);
    }

    @Transactional
    public void deleteById(Long id) {
        incidenciaRepository.deleteById(id);
    }
}