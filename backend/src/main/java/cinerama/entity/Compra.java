package cinerama.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "COMPRA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMPRA")
    private Long idCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "compras"})
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FUNCION", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Funcion funcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ASIENTO")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Asiento asiento;

    @Column(name = "PRECIO", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "METODO_PAGO", length = 50)
    private String metodoPago;

    @Builder.Default
    @Column(name = "ESTADO", length = 50)
    private String estado = "PENDIENTE";

    @Column(name = "CODIGO_QR", length = 500)
    private String codigoQr;

    @Column(name = "FECHA_COMPRA", nullable = false)
    private Timestamp fechaCompra;

    @Column(name = "PRODUCTOS", length = 2000)
    private String productos;

    // Dulcería del pedido. Se guarda una sola vez (en la primera butaca) para que
    // el total del grupo sea la suma de PRECIO + TOTAL_PRODUCTOS.
    @Column(name = "TOTAL_PRODUCTOS", precision = 10, scale = 2)
    private BigDecimal totalProductos;

    @PrePersist
    public void prePersist() {
        if (this.fechaCompra == null) {
            this.fechaCompra = new Timestamp(System.currentTimeMillis());
        }
        if (this.estado == null || this.estado.isBlank()) {
            this.estado = "PENDIENTE";
        }
    }

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_COMPLETADA = "COMPLETADA";
    public static final String ESTADO_CANCELADA = "CANCELADA";
}