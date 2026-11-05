package cinerama.dto;

import cinerama.entity.Compra;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Respuesta plana de una compra. El frontend consume estos campos directamente,
 * y al no exponer las entidades se evitan las relaciones LAZY fuera de sesión.
 */
public record CompraResponse(
        Long idCompra,
        Long idCliente,
        String nombreCliente,
        Long idFuncion,
        String tituloPelicula,
        String nombreSala,
        LocalDateTime horaInicio,
        Long idAsiento,
        String filaAsiento,
        Integer numeroAsiento,
        BigDecimal precio,
        BigDecimal totalProductos,
        String metodoPago,
        String estado,
        String codigoQr,
        LocalDateTime fechaCompra,
        String productos
) {
    public static CompraResponse from(Compra compra) {
        var funcion = compra.getFuncion();
        var asiento = compra.getAsiento();

        return new CompraResponse(
                compra.getIdCompra(),
                compra.getCliente().getIdCliente(),
                compra.getCliente().getNombre(),
                funcion.getIdFuncion(),
                funcion.getPelicula().getTitulo(),
                funcion.getSala().getNombre(),
                funcion.getHoraInicio(),
                asiento != null ? asiento.getIdAsiento() : null,
                asiento != null ? asiento.getFila() : null,
                asiento != null ? asiento.getNumero() : null,
                compra.getPrecio(),
                compra.getTotalProductos(),
                compra.getMetodoPago(),
                compra.getEstado(),
                compra.getCodigoQr(),
                compra.getFechaCompra() != null ? compra.getFechaCompra().toLocalDateTime() : null,
                compra.getProductos()
        );
    }
}
