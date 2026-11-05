package cinerama.service;

import cinerama.dto.CompraRequest;
import cinerama.entity.Asiento;
import cinerama.entity.Cliente;
import cinerama.entity.Compra;
import cinerama.entity.Funcion;
import cinerama.entity.Producto;
import cinerama.entity.Promocion;
import cinerama.exception.BadRequestException;
import cinerama.exception.ResourceNotFoundException;
import cinerama.repository.AsientoRepository;
import cinerama.repository.ClienteRepository;
import cinerama.repository.CompraRepository;
import cinerama.repository.FuncionRepository;
import cinerama.repository.ProductoRepository;
import cinerama.repository.PromocionRepository;
import cinerama.util.EmailUtil;
import cinerama.util.QRUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompraServiceImpl implements ICompraService {

    private static final BigDecimal PRECIO_BASE = BigDecimal.valueOf(20.00);
    private static final BigDecimal PRECIO_ESTRENO = BigDecimal.valueOf(25.00);
    private static final int MAX_LONGITUD_PRODUCTOS = 2000;
    private static final List<String> ESTADOS_OCUPADO =
            List.of("COMPLETADA", "CONFIRMADA", "PENDIENTE");

    private final CompraRepository compraRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionRepository funcionRepository;
    private final AsientoRepository asientoRepository;
    private final ProductoRepository productoRepository;
    private final PromocionRepository promocionRepository;
    private final EmailUtil emailUtil;

    public List<Compra> findAll() {
        return compraRepository.findAllConRelaciones();
    }

    public List<Compra> findByCliente(Long idCliente) {
        return compraRepository.findByClienteConRelaciones(idCliente);
    }

    public List<Compra> findByFuncion(Long idFuncion) {
        return compraRepository.findByFuncionConRelaciones(idFuncion);
    }

    public List<Compra> findConfirmedByFuncion(Long idFuncion) {
        return compraRepository.findConfirmedByFuncion(idFuncion);
    }

    public Optional<Compra> findByCodigoQr(String codigoQr) {
        return compraRepository.findFirstByCodigoQrOrderByIdCompraAsc(codigoQr);
    }

    public Compra findById(Long id) {
        return compraRepository.findByIdConRelaciones(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", id));
    }

    @Transactional
    public Compra save(Compra compra) {
        return compraRepository.save(compra);
    }

    @Transactional
    public Compra update(Compra compra) {
        return compraRepository.save(compra);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!compraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Compra", id);
        }
        compraRepository.deleteById(id);
    }

    public boolean isAsientoOcupado(Long idFuncion, Long idAsiento) {
        return compraRepository.existsByFuncion_IdFuncionAndAsiento_IdAsientoAndEstadoIn(
                idFuncion, idAsiento, ESTADOS_OCUPADO);
    }

    @Transactional
    public List<Compra> procesarCompra(CompraRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.getClienteId()));

        Funcion funcion = funcionRepository.findByIdWithRelations(request.getFuncionId())
                .orElseThrow(() -> new ResourceNotFoundException("Función", request.getFuncionId()));

        if (funcion.getActivo() != null && funcion.getActivo() == 0) {
            throw new BadRequestException("La función ya no está disponible");
        }

        List<Long> idsSolicitados = request.getAsientosIds() == null ? List.of() : request.getAsientosIds();
        if (idsSolicitados.isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos un asiento");
        }

        // Evita comprar dos veces la misma butaca dentro de una sola petición.
        LinkedHashSet<Long> idsUnicos = new LinkedHashSet<>(idsSolicitados);
        if (idsUnicos.size() != idsSolicitados.size()) {
            throw new BadRequestException("No se puede repetir el mismo asiento en la compra");
        }

        List<Asiento> asientos = asientoRepository.findAllById(idsUnicos);
        if (asientos.size() != idsUnicos.size()) {
            throw new BadRequestException("Uno o más asientos no existen");
        }

        for (Asiento asiento : asientos) {
            if (!asiento.getSala().getIdSala().equals(funcion.getSala().getIdSala())) {
                throw new BadRequestException(
                        "El asiento " + asiento.getFila() + asiento.getNumero() + " no pertenece a la sala de la función");
            }
            if (isAsientoOcupado(funcion.getIdFuncion(), asiento.getIdAsiento())) {
                throw new BadRequestException(
                        "El asiento " + asiento.getFila() + asiento.getNumero() + " ya está ocupado para esta función");
            }
        }

        BigDecimal precioUnitario = calcularPrecioUnitario(funcion, request.getCodigoPromocion());

        BigDecimal totalDulceria = BigDecimal.ZERO;
        String textoProductos = null;
        if (request.getProductos() != null && !request.getProductos().isEmpty()) {
            StringBuilder texto = new StringBuilder();
            for (CompraRequest.ItemDulceria item : request.getProductos()) {
                Producto producto = productoRepository.findById(item.idProducto())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto", item.idProducto()));
                if (producto.getActivo() != null && producto.getActivo() == 0) {
                    throw new BadRequestException("El producto " + producto.getNombre() + " ya no está disponible");
                }
                int cantidad = item.cantidad() == null ? 0 : item.cantidad();
                if (cantidad < 1) {
                    throw new BadRequestException("La cantidad del producto " + producto.getNombre() + " no es válida");
                }
                BigDecimal precio = producto.getPrecio() == null ? BigDecimal.ZERO : producto.getPrecio();
                totalDulceria = totalDulceria.add(precio.multiply(BigDecimal.valueOf(cantidad)));
                if (!texto.isEmpty()) {
                    texto.append(", ");
                }
                texto.append(producto.getNombre()).append(" x").append(cantidad);
            }
            textoProductos = texto.toString();
            if (textoProductos.length() > MAX_LONGITUD_PRODUCTOS) {
                textoProductos = textoProductos.substring(0, MAX_LONGITUD_PRODUCTOS);
            }
        }

        // Código corto: el QR se genera al vuelo en GET /api/compras/{id}/qr.png
        String codigoQr = "CIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        Timestamp fechaCompra = Timestamp.valueOf(LocalDateTime.now());
        String metodoPago = request.getMetodoPago() == null
                ? "TARJETA"
                : request.getMetodoPago().trim().toUpperCase(Locale.ROOT);

        BigDecimal totalProductos = totalDulceria.setScale(2, RoundingMode.HALF_UP);

        List<Compra> compras = new ArrayList<>();
        boolean primera = true;
        for (Asiento asiento : asientos) {
            // El total de dulcería se guarda una sola vez para no duplicarlo al sumar.
            BigDecimal productosLinea = primera ? totalProductos : null;
            primera = false;

            compras.add(compraRepository.save(crearLinea(
                    cliente, funcion, asiento, precioUnitario, productosLinea, metodoPago,
                    codigoQr, fechaCompra, textoProductos)));
        }

        BigDecimal totalEntradas = precioUnitario.multiply(BigDecimal.valueOf(asientos.size()));
        enviarCorreo(cliente, funcion, asientos, totalEntradas.add(totalProductos), codigoQr, fechaCompra);

        return compras;
    }

    private Compra crearLinea(Cliente cliente, Funcion funcion, Asiento asiento, BigDecimal precio,
                              BigDecimal totalProductos, String metodoPago, String codigoQr,
                              Timestamp fechaCompra, String productos) {
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setFuncion(funcion);
        compra.setAsiento(asiento);
        compra.setPrecio(precio);
        compra.setTotalProductos(totalProductos);
        compra.setMetodoPago(metodoPago);
        compra.setEstado(Compra.ESTADO_COMPLETADA);
        compra.setCodigoQr(codigoQr);
        compra.setFechaCompra(fechaCompra);
        compra.setProductos(productos);
        return compra;
    }

    private BigDecimal calcularPrecioUnitario(Funcion funcion, String codigoPromocion) {
        Integer duracion = funcion.getPelicula().getDuracionMinutos();
        BigDecimal base = duracion != null && duracion > 120 ? PRECIO_ESTRENO : PRECIO_BASE;

        if (codigoPromocion == null || codigoPromocion.isBlank()) {
            return base;
        }

        Promocion promocion = promocionRepository
                .findByCodigoIgnoreCase(codigoPromocion.trim())
                .orElseThrow(() -> new BadRequestException("El código de promoción no existe"));

        if (promocion.getActivo() != null && promocion.getActivo() == 0) {
            throw new BadRequestException("La promoción ya no está vigente");
        }

        int descuento = promocion.getDescuento() == null ? 0 : promocion.getDescuento();
        descuento = Math.max(0, Math.min(100, descuento));

        BigDecimal factor = BigDecimal.valueOf(100 - descuento);
        return base.multiply(factor)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private void enviarCorreo(Cliente cliente, Funcion funcion, List<Asiento> asientos,
                              BigDecimal total, String codigoQr, Timestamp fechaCompra) {
        try {
            String asientosStr = asientos.stream()
                    .map(a -> a.getFila() + a.getNumero())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            String fechaStr = fechaCompra.toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            emailUtil.enviarConfirmacionCompra(
                    cliente.getEmail(),
                    cliente.getNombre(),
                    funcion.getPelicula().getTitulo(),
                    funcion.getSala().getNombre(),
                    fechaStr,
                    asientosStr,
                    total.doubleValue(),
                    codigoQr,
                    QRUtil.generarQRBytes(codigoQr)
            );
        } catch (Exception e) {
            // La compra ya quedó registrada: un fallo de correo no debe romperla.
            System.err.println("Error enviando correo de confirmación: " + e.getMessage());
        }
    }
}
