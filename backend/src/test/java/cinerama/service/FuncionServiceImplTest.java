package cinerama.service;

import cinerama.dto.FuncionRequest;
import cinerama.entity.Funcion;
import cinerama.entity.Pelicula;
import cinerama.entity.Sala;
import cinerama.exception.BadRequestException;
import cinerama.repository.FuncionRepository;
import cinerama.repository.PeliculaRepository;
import cinerama.repository.SalaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FuncionServiceImplTest {

    @Mock
    private FuncionRepository funcionRepository;
    @Mock
    private PeliculaRepository peliculaRepository;
    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private FuncionServiceImpl service;

    private Sala sala;
    private Pelicula pelicula;

    @BeforeEach
    void setUp() {
        sala = new Sala();
        sala.setIdSala(1L);
        sala.setNombre("Sala 1");

        pelicula = new Pelicula();
        pelicula.setIdPelicula(1L);
        pelicula.setTitulo("Dune");
        pelicula.setDuracionMinutos(120);
    }

    private Funcion funcionExistente(LocalDateTime inicio) {
        Funcion funcion = new Funcion();
        funcion.setIdFuncion(9L);
        funcion.setSala(sala);
        funcion.setPelicula(pelicula);
        funcion.setHoraInicio(inicio);
        funcion.setActivo(1);
        return funcion;
    }

    @Test
    void rechazaFuncionQueSePisaConOtraDeLaMismaSala() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 10, 19, 0);
        given(peliculaRepository.findById(1L)).willReturn(Optional.of(pelicula));
        given(salaRepository.findById(1L)).willReturn(Optional.of(sala));
        given(funcionRepository.findActivasEnRango(eq(1L), any(), any()))
                .willReturn(List.of(funcionExistente(inicio.minusMinutes(30))));

        FuncionRequest request = new FuncionRequest(1L, 1L, inicio, "PROGRAMADA", 1);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("ya tiene");

        verify(funcionRepository, never()).save(any());
    }

    @Test
    void permiteFuncionCuandoElHorarioEstaLibre() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 10, 19, 0);
        given(peliculaRepository.findById(1L)).willReturn(Optional.of(pelicula));
        given(salaRepository.findById(1L)).willReturn(Optional.of(sala));
        given(funcionRepository.findActivasEnRango(eq(1L), any(), any()))
                .willReturn(List.of(funcionExistente(inicio.minusMinutes(300))));

        AtomicReference<Funcion> guardada = new AtomicReference<>();
        given(funcionRepository.save(any(Funcion.class))).willAnswer(invocacion -> {
            Funcion f = invocacion.getArgument(0);
            f.setIdFuncion(10L);
            guardada.set(f);
            return f;
        });
        given(funcionRepository.findByIdWithRelations(10L))
                .willAnswer(invocacion -> Optional.of(guardada.get()));

        FuncionRequest request = new FuncionRequest(1L, 1L, inicio, "PROGRAMADA", 1);

        Funcion resultado = service.save(request);

        assertThat(resultado.getHoraInicio()).isEqualTo(inicio);
        assertThat(resultado.getSala().getIdSala()).isEqualTo(1L);
        verify(funcionRepository).save(any(Funcion.class));
    }

    @Test
    void alEditarNoSeComparaConsigoMisma() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 10, 19, 0);
        Funcion propia = funcionExistente(inicio);
        given(funcionRepository.findByIdWithRelations(9L)).willReturn(Optional.of(propia));
        given(peliculaRepository.findById(1L)).willReturn(Optional.of(pelicula));
        given(salaRepository.findById(1L)).willReturn(Optional.of(sala));
        given(funcionRepository.findActivasEnRango(eq(1L), any(), any())).willReturn(List.of(propia));
        given(funcionRepository.save(any(Funcion.class))).willAnswer(i -> i.getArgument(0));

        FuncionRequest request = new FuncionRequest(1L, 1L, inicio, "PROGRAMADA", 1);

        assertThat(service.update(9L, request).getIdFuncion()).isEqualTo(9L);
    }
}
