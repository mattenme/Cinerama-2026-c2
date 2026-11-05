package cinerama.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SALA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SALA")
    private Long idSala;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "TIPO", length = 50)
    private String tipo;

    @Builder.Default
    @Column(name = "CAPACIDAD_TOTAL", nullable = false)
    private Integer capacidadTotal = 0;

    @Builder.Default
    @Column(name = "ACTIVO", nullable = false)
    private Integer activo = 1;
}