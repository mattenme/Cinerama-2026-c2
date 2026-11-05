package cinerama.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private boolean success;
    private String message;
    private String mensaje;
    private String rol;
    private Long clienteId;
    private Long id;
    private String nombre;
    private String email;
    private boolean needVerify;
    /** Solo se envía cuando el correo no está configurado (modo desarrollo). */
    private String codigoDesarrollo;
}