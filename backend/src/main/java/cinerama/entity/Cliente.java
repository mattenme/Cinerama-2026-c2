package cinerama.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CLIENTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CLIENTE")
    private Long idCliente;

    @Column(name = "DNI", unique = true, length = 20)
    private String dni;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Column(name = "EMAIL", unique = true, length = 150)
    private String email;

    @Column(name = "TELEFONO", length = 20)
    private String telefono;

    @Column(name = "CONTRASENA", length = 255)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;

    @Builder.Default
    @Column(name = "ROL", length = 20)
    private String rol = "CLIENTE";

    @Builder.Default
    @Column(name = "ACTIVO", nullable = false)
    private Integer activo = 1;

    @Builder.Default
    @Column(name = "VERIFICADO", nullable = false)
    private Integer verificado = 0;

    @Column(name = "CODIGO_VERIFICACION", length = 100)
    private String codigoVerificacion;
}