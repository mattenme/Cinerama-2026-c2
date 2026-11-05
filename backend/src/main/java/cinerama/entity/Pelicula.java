package cinerama.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PELICULA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PELICULA")
    private Long idPelicula;

    @Column(name = "TITULO", nullable = false, length = 200)
    private String titulo;

    @Column(name = "DURACION_MINUTOS", nullable = false)
    private Integer duracionMinutos;

    @Column(name = "GENERO", length = 100)
    private String genero;

    @Column(name = "SINOPSIS", length = 2000)
    private String sinopsis;

    @Column(name = "IMAGEN_URL", length = 500)
    private String imagenUrl;

    @Builder.Default
    @Column(name = "DESTACADO", nullable = false)
    private Integer destacado = 0;

    @Builder.Default
    @Column(name = "ACTIVO", nullable = false)
    private Integer activo = 1;
}