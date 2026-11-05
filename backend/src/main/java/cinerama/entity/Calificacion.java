package cinerama.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "CALIFICACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(CalificacionId.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Calificacion {

    @Id
    @Column(name = "ID_CLIENTE", nullable = false)
    private Long idCliente;

    @Id
    @Column(name = "ID_PELICULA", nullable = false)
    private Long idPelicula;

    @Column(name = "PUNTUACION", nullable = false)
    private Integer puntuacion;

    @Column(name = "COMENTARIO", length = 2000)
    private String comentario;

    @Column(name = "FECHA_CALIFICACION", nullable = false)
    private Timestamp fechaCalificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE", insertable = false, updatable = false)
    @JsonIgnore
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PELICULA", insertable = false, updatable = false)
    @JsonIgnore
    private Pelicula pelicula;

    @PrePersist
    public void prePersist() {
        if (this.fechaCalificacion == null) {
            this.fechaCalificacion = new Timestamp(System.currentTimeMillis());
        }
    }
}