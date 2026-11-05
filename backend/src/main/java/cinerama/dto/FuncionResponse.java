package cinerama.dto;

import cinerama.entity.Funcion;

import java.time.LocalDateTime;

public record FuncionResponse(
        Long idFuncion,
        Long idPelicula,
        String tituloPelicula,
        Long idSala,
        String nombreSala,
        LocalDateTime horaInicio,
        String estado,
        Integer activo
) {
    public static FuncionResponse from(Funcion funcion) {
        return new FuncionResponse(
                funcion.getIdFuncion(),
                funcion.getPelicula().getIdPelicula(),
                funcion.getPelicula().getTitulo(),
                funcion.getSala().getIdSala(),
                funcion.getSala().getNombre(),
                funcion.getHoraInicio(),
                funcion.getEstado(),
                funcion.getActivo()
        );
    }
}
