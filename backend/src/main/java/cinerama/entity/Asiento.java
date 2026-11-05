package cinerama.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ASIENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ASIENTO")
    private Long idAsiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SALA", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Sala sala;

    @Column(name = "FILA", nullable = false, length = 5)
    private String fila;

    @Column(name = "NUMERO", nullable = false)
    private Integer numero;

    // El esquema de Oracle valida estos valores exactos (TG_VALIDAR_ESTADO_ASIENTO):
    // Disponible, Seleccionada, Vendida, Mantenimiento.
    @Builder.Default
    @Column(name = "ESTADO", length = 20)
    private String estado = "Disponible";
}