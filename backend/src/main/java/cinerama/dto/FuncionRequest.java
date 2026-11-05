package cinerama.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Cuerpo para crear/editar una función. El frontend envía los IDs de película y
 * sala, no las entidades anidadas.
 */
public record FuncionRequest(
        @NotNull(message = "La película es obligatoria")
        Long idPelicula,

        @NotNull(message = "La sala es obligatoria")
        Long idSala,

        @NotNull(message = "La fecha y hora son obligatorias")
        LocalDateTime horaInicio,

        String estado,

        Integer activo
) {
}
