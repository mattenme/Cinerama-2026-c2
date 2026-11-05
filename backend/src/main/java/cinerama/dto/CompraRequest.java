package cinerama.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraRequest {
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El ID de la función es obligatorio")
    private Long funcionId;

    @NotNull(message = "La lista de asientos es obligatoria")
    @Size(min = 1, message = "Debe seleccionar al menos un asiento")
    private List<Long> asientosIds;

    @NotNull(message = "El método de pago es obligatorio")
    private String metodoPago;

    /** Dulcería pre-ordenada: el backend calcula el precio con la base de datos. */
    @Valid
    private List<ItemDulceria> productos;

    private String codigoPromocion;

    public record ItemDulceria(
            @NotNull(message = "El producto es obligatorio")
            Long idProducto,

            @NotNull(message = "La cantidad es obligatoria")
            @Min(value = 1, message = "La cantidad debe ser al menos 1")
            Integer cantidad
    ) {
    }
}