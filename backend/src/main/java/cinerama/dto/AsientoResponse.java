package cinerama.dto;

import cinerama.entity.Asiento;

public record AsientoResponse(
        Long idAsiento,
        Long idSala,
        String nombreSala,
        String fila,
        Integer numero,
        String estado
) {
    public static AsientoResponse from(Asiento asiento) {
        return new AsientoResponse(
                asiento.getIdAsiento(),
                asiento.getSala().getIdSala(),
                asiento.getSala().getNombre(),
                asiento.getFila(),
                asiento.getNumero(),
                asiento.getEstado()
        );
    }
}
