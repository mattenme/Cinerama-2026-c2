package cinerama.config;

import cinerama.entity.Asiento;
import cinerama.entity.Cliente;
import cinerama.entity.Funcion;
import cinerama.entity.Pelicula;
import cinerama.entity.Producto;
import cinerama.entity.Promocion;
import cinerama.entity.Sala;
import cinerama.repository.AsientoRepository;
import cinerama.repository.ClienteRepository;
import cinerama.repository.FuncionRepository;
import cinerama.repository.PeliculaRepository;
import cinerama.repository.ProductoRepository;
import cinerama.repository.PromocionRepository;
import cinerama.repository.SalaRepository;
import cinerama.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Datos mínimos para que el sistema sea usable en local: sin salas ni butacas
 * no se puede comprar, y sin funciones la cartelera aparece vacía.
 * Solo rellena tablas vacías, así que no duplica ni pisa datos existentes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "cinerama.seed", name = "demo-data", havingValue = "true", matchIfMissing = true)
public class DemoDataInitializer implements ApplicationRunner {

    private static final String[] FILAS = {"A", "B", "C", "D", "E", "F"};
    private static final LocalTime[] HORARIOS = {
            LocalTime.of(14, 0), LocalTime.of(17, 30), LocalTime.of(20, 30)
    };

    private final SalaRepository salaRepository;
    private final AsientoRepository asientoRepository;
    private final ProductoRepository productoRepository;
    private final PromocionRepository promocionRepository;
    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordUtil passwordUtil;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        crearClienteDemo();
        List<Sala> salas = crearSalas();
        crearProductos();
        crearPromociones();
        crearFunciones(salas);
    }

    private void crearClienteDemo() {
        if (clienteRepository.findByDni("77889901").isPresent()) {
            return;
        }
        Cliente cliente = Cliente.builder()
                .dni("77889901")
                .nombre("Cliente de Prueba")
                .email("cliente@cinerama.pe")
                .telefono("999888777")
                .contrasena(passwordUtil.hash("123456"))
                .rol("CLIENTE")
                .activo(1)
                .verificado(1)
                .build();
        clienteRepository.save(cliente);
        log.info("Datos demo: cliente 77889901 / 123456 creado");
    }

    private List<Sala> crearSalas() {
        if (salaRepository.count() > 0) {
            return salaRepository.findAll();
        }

        List<Sala> salas = new ArrayList<>();
        salas.add(crearSala("Sala 1 - 2D", "2D", 6, 10));
        salas.add(crearSala("Sala 2 - 3D", "3D", 6, 8));
        salas.add(crearSala("Sala 3 - VIP", "VIP", 4, 6));
        log.info("Datos demo: {} salas con butacas creadas", salas.size());
        return salas;
    }

    private Sala crearSala(String nombre, String tipo, int filas, int butacasPorFila) {
        Sala sala = Sala.builder()
                .nombre(nombre)
                .tipo(tipo)
                .capacidadTotal(filas * butacasPorFila)
                .activo(1)
                .build();
        sala = salaRepository.save(sala);

        List<Asiento> asientos = new ArrayList<>();
        for (int f = 0; f < filas; f++) {
            for (int numero = 1; numero <= butacasPorFila; numero++) {
                asientos.add(Asiento.builder()
                        .sala(sala)
                        .fila(FILAS[f])
                        .numero(numero)
                        .estado("Disponible")
                        .build());
            }
        }
        asientoRepository.saveAll(asientos);
        return sala;
    }

    private void crearProductos() {
        if (productoRepository.count() > 0) {
            return;
        }
        productoRepository.saveAll(List.of(
                producto("Combo Familiar", "2 canchas grandes + 2 gaseosas", "35.90", "Combos"),
                producto("Combo Personal", "1 cancha mediana + 1 gaseosa", "19.90", "Combos"),
                producto("Cancha Grande", "Cancha grande de maíz", "8.50", "Snacks"),
                producto("Gaseosa Grande", "Bebida de 1 litro", "6.00", "Bebidas"),
                producto("Chocolate", "Barra de chocolate", "5.00", "Snacks"),
                producto("Agua Mineral", "Botella de 600 ml", "4.00", "Bebidas")
        ));
        log.info("Datos demo: dulcería creada");
    }

    private Producto producto(String nombre, String descripcion, String precio, String categoria) {
        return Producto.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(new BigDecimal(precio))
                .categoria(categoria)
                .activo(1)
                .build();
    }

    private void crearPromociones() {
        if (promocionRepository.count() > 0) {
            return;
        }
        promocionRepository.saveAll(List.of(
                Promocion.builder()
                        .codigo("CINERAMA10")
                        .descripcion("10% de descuento en tus butacas")
                        .descuento(10)
                        .activo(1)
                        .build(),
                Promocion.builder()
                        .codigo("ESTUDIANTE15")
                        .descripcion("15% de descuento con carné de estudiante")
                        .descuento(15)
                        .activo(1)
                        .build()
        ));
        log.info("Datos demo: promociones creadas");
    }

    private void crearFunciones(List<Sala> salas) {
        if (funcionRepository.count() > 0 || salas.isEmpty()) {
            return;
        }

        List<Pelicula> peliculas = peliculaRepository.findByActivo(1);
        if (peliculas.isEmpty()) {
            log.info("Datos demo: no hay películas activas, no se crean funciones");
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        List<Funcion> funciones = new ArrayList<>();
        int indiceSala = 0;

        for (Pelicula pelicula : peliculas.stream().limit(3).toList()) {
            for (int dia = 0; dia < 7; dia++) {
                LocalDate fecha = LocalDate.now().plusDays(dia);
                for (LocalTime hora : HORARIOS) {
                    LocalDateTime inicio = LocalDateTime.of(fecha, hora);
                    if (inicio.isBefore(ahora.plusMinutes(30))) {
                        continue;
                    }
                    Sala sala = salas.get(indiceSala % salas.size());
                    indiceSala++;

                    funciones.add(Funcion.builder()
                            .pelicula(pelicula)
                            .sala(sala)
                            .horaInicio(inicio)
                            .estado("PROGRAMADA")
                            .activo(1)
                            .build());
                }
            }
        }

        funcionRepository.saveAll(funciones);
        log.info("Datos demo: {} funciones programadas", funciones.size());
    }
}
