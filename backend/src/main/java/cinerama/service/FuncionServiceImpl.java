package cinerama.service;

import cinerama.dto.FuncionRequest;
import cinerama.entity.Funcion;
import cinerama.entity.Pelicula;
import cinerama.entity.Sala;
import cinerama.exception.BadRequestException;
import cinerama.exception.ResourceNotFoundException;
import cinerama.repository.FuncionRepository;
import cinerama.repository.PeliculaRepository;
import cinerama.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuncionServiceImpl implements IFuncionService {

    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;

    public List<Funcion> findAll() {
        return funcionRepository.findAllWithRelations();
    }

    public List<Funcion> findAllActive() {
        return funcionRepository.findAllActive();
    }

    public List<Funcion> findByPelicula(Long idPelicula) {
        return funcionRepository.findByPeliculaIdActive(idPelicula);
    }

    public List<Funcion> findBySala(Long idSala) {
        return funcionRepository.findBySalaIdActiveWithRelations(idSala);
    }

    public Funcion findById(Long id) {
        return funcionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Función", id));
    }

    @Transactional
    public Funcion save(FuncionRequest request) {
        Funcion funcion = aplicarDatos(new Funcion(), request);
        Funcion guardada = funcionRepository.save(funcion);
        return findById(guardada.getIdFuncion());
    }

    @Transactional
    public Funcion update(Long id, FuncionRequest request) {
        Funcion funcion = findById(id);
        aplicarDatos(funcion, request);
        funcionRepository.save(funcion);
        return findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!funcionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Función", id);
        }
        funcionRepository.deleteById(id);
    }

    @Transactional
    public Funcion toggleActivo(Long id) {
        Funcion funcion = findById(id);
        funcion.setActivo(funcion.getActivo() == 1 ? 0 : 1);
        return funcionRepository.save(funcion);
    }

    private Funcion aplicarDatos(Funcion funcion, FuncionRequest request) {
        Pelicula pelicula = peliculaRepository.findById(request.idPelicula())
                .orElseThrow(() -> new ResourceNotFoundException("Película", request.idPelicula()));

        Sala sala = salaRepository.findById(request.idSala())
                .orElseThrow(() -> new ResourceNotFoundException("Sala", request.idSala()));

        if (request.horaInicio() == null) {
            throw new BadRequestException("La fecha y hora son obligatorias");
        }

        funcion.setPelicula(pelicula);
        funcion.setSala(sala);
        funcion.setHoraInicio(request.horaInicio());

        String estado = request.estado();
        funcion.setEstado(estado == null || estado.isBlank()
                ? "PROGRAMADA"
                : estado.trim().toUpperCase(Locale.ROOT));

        funcion.setActivo(request.activo() == null ? 1 : request.activo());

        validarChoqueDeHorario(funcion);
        return funcion;
    }

    /**
     * Impide programar dos funciones que se pisen en la misma sala, teniendo en
     * cuenta la duración de cada película.
     */
    private void validarChoqueDeHorario(Funcion funcion) {
        LocalDateTime inicio = funcion.getHoraInicio();
        LocalDateTime fin = inicio.plusMinutes(duracionDe(funcion.getPelicula()));

        List<Funcion> cercanas = funcionRepository.findActivasEnRango(
                funcion.getSala().getIdSala(), inicio.minusHours(6), inicio.plusHours(6));

        for (Funcion otra : cercanas) {
            if (otra.getIdFuncion() != null && otra.getIdFuncion().equals(funcion.getIdFuncion())) {
                continue;
            }
            LocalDateTime otraInicio = otra.getHoraInicio();
            LocalDateTime otraFin = otraInicio.plusMinutes(duracionDe(otra.getPelicula()));

            if (inicio.isBefore(otraFin) && fin.isAfter(otraInicio)) {
                DateTimeFormatter hora = DateTimeFormatter.ofPattern("HH:mm");
                throw new BadRequestException(String.format(
                        "La sala %s ya tiene \"%s\" de %s a %s; elige otro horario",
                        funcion.getSala().getNombre(),
                        otra.getPelicula().getTitulo(),
                        otraInicio.format(hora), otraFin.format(hora)));
            }
        }
    }

    private int duracionDe(Pelicula pelicula) {
        Integer duracion = pelicula != null ? pelicula.getDuracionMinutos() : null;
        return duracion != null && duracion > 0 ? duracion : 120;
    }
}
