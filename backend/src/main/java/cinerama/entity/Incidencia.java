package cinerama.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "INCIDENCIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INCIDENCIA")
    private Long idIncidencia;

    @Column(name = "TIPO", nullable = false, length = 100)
    private String tipo;

    @Column(name = "DESCRIPCION", length = 2000)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SALA")
    @JsonIgnore
    private Sala sala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FUNCION")
    @JsonIgnore
    private Funcion funcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    @JsonIgnore
    private Cliente cliente;

    @Column(name = "FECHA_REPORTE", nullable = false)
    private Timestamp fechaReporte;

    @Builder.Default
    @Column(name = "ESTADO", length = 50)
    private String estado = "PENDIENTE";

    @PrePersist
    public void prePersist() {
        if (this.fechaReporte == null) {
            this.fechaReporte = new Timestamp(System.currentTimeMillis());
        }
        if (this.estado == null || this.estado.isBlank()) {
            this.estado = "PENDIENTE";
        }
    }
}