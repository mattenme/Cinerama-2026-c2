package cinerama.service;

import cinerama.entity.Calificacion;
import cinerama.entity.CalificacionId;
import cinerama.repository.CalificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalificacionServiceImpl implements ICalificacionService {

    private final CalificacionRepository calificacionRepository;

    public List<Calificacion> findAll() {
        return calificacionRepository.findAll();
    }

    public List<Calificacion> findByPelicula(Long idPelicula) {
        return calificacionRepository.findByIdPelicula(idPelicula);
    }

    public List<Calificacion> findByCliente(Long idCliente) {
        return calificacionRepository.findByIdCliente(idCliente);
    }

    public Optional<Calificacion> findByClienteAndPelicula(Long idCliente, Long idPelicula) {
        return calificacionRepository.findByIdClienteAndIdPelicula(idCliente, idPelicula);
    }

    public Double getPromedioByPelicula(Long idPelicula) {
        return calificacionRepository.findAverageByPelicula(idPelicula);
    }

    public Long getCountByPelicula(Long idPelicula) {
        return calificacionRepository.countByPelicula(idPelicula);
    }

    @Transactional
    public Calificacion save(Calificacion calificacion) {
        return calificacionRepository.save(calificacion);
    }

    @Transactional
    public Calificacion update(Calificacion calificacion) {
        return calificacionRepository.save(calificacion);
    }

    @Transactional
    public void deleteById(Long idCliente, Long idPelicula) {
        calificacionRepository.deleteById(new CalificacionId(idCliente, idPelicula));
    }

    @Transactional
    public Calificacion upsert(Calificacion calificacion) {
        Optional<Calificacion> existing = calificacionRepository.findByIdClienteAndIdPelicula(
                calificacion.getIdCliente(), calificacion.getIdPelicula());
        if (existing.isPresent()) {
            Calificacion toUpdate = existing.get();
            toUpdate.setPuntuacion(calificacion.getPuntuacion());
            toUpdate.setComentario(calificacion.getComentario());
            return calificacionRepository.save(toUpdate);
        }
        return calificacionRepository.save(calificacion);
    }
}