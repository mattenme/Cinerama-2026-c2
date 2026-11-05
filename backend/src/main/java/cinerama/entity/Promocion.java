package cinerama.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PROMOCION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PROMOCION")
    private Long idPromocion;

    @Column(name = "CODIGO", unique = true, nullable = false, length = 50)
    private String codigo;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @Column(name = "DESCUENTO", nullable = false)
    private Integer descuento;

    @Builder.Default
    @Column(name = "ACTIVO", nullable = false)
    private Integer activo = 1;
}