package cinerama.controller;

import cinerama.dto.*;
import cinerama.entity.Cliente;
import cinerama.service.IClienteService;
import cinerama.util.EmailUtil;
import cinerama.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IClienteService clienteService;
    private final EmailUtil emailUtil;
    private final PasswordUtil passwordUtil;

    /** En desarrollo, si el correo no está configurado se devuelve el código. */
    @Value("${cinerama.mail.expose-dev-code:true}")
    private boolean exponerCodigoDesarrollo;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Cliente cliente = clienteService.findByDni(request.getDni().trim())
                .orElse(null);

        if (cliente == null || !passwordUtil.check(request.getContrasena(), cliente.getContrasena())) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .message("DNI o contraseña incorrectos")
                    .build());
        }

        if (Integer.valueOf(0).equals(cliente.getActivo())) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .message("La cuenta está desactivada")
                    .build());
        }

        if (Integer.valueOf(0).equals(cliente.getVerificado())) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .message("La cuenta no ha sido verificada. Revisa tu correo.")
                    .build());
        }

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("clienteId", cliente.getIdCliente());
        session.setAttribute("rol", cliente.getRol());
        session.setAttribute("nombre", cliente.getNombre());

        return ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .message("Inicio de sesión exitoso")
                .rol(cliente.getRol())
                .clienteId(cliente.getIdCliente())
                .nombre(cliente.getNombre())
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (clienteService.existsByDni(request.getDni().trim())) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .message("El DNI ya está registrado")
                    .build());
        }
        if (clienteService.existsByEmail(request.getEmail().trim())) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .message("El email ya está registrado")
                    .build());
        }

        Cliente cliente = new Cliente();
        cliente.setDni(request.getDni().trim());
        cliente.setNombre(request.getNombre().trim());
        cliente.setEmail(request.getEmail().trim());
        cliente.setTelefono(request.getTelefono());
        cliente.setContrasena(passwordUtil.hash(request.getContrasena()));
        cliente.setRol("CLIENTE");
        cliente.setActivo(1);
        cliente.setVerificado(0);
        cliente.setCodigoVerificacion(generarCodigo6Digitos());

        clienteService.save(cliente);

        boolean enviado = emailUtil.enviarCodigoVerificacion(cliente.getEmail(), cliente.getCodigoVerificacion());

        AuthResponse.AuthResponseBuilder respuesta = AuthResponse.builder().success(true);
        if (enviado) {
            respuesta.message("Registro exitoso. Se ha enviado un código de verificación a tu correo.");
        } else if (exponerCodigoDesarrollo) {
            // Sin servidor de correo configurado el usuario no podría verificar su cuenta.
            respuesta.message("Registro exitoso. El correo no está configurado; usa el código de verificación mostrado.")
                    .codigoDesarrollo(cliente.getCodigoVerificacion());
        } else {
            respuesta.message("Registro exitoso, pero no se pudo enviar el correo de verificación.");
        }
        return ResponseEntity.ok(respuesta.build());
    }

    @PostMapping("/verificar")
    public ResponseEntity<AuthResponse> verificar(@Valid @RequestBody VerificarRequest request) {
        Cliente cliente = clienteService.findByEmail(request.getEmail().trim())
                .orElse(null);

        if (cliente == null) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .message("Email no encontrado")
                    .build());
        }

        if (Integer.valueOf(1).equals(cliente.getVerificado())) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .message("La cuenta ya está verificada")
                    .build());
        }

        if (cliente.getCodigoVerificacion() == null || !cliente.getCodigoVerificacion().equals(request.getCodigo())) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .message("Código de verificación incorrecto o expirado")
                    .build());
        }

        clienteService.verificarEmail(cliente.getIdCliente());

        return ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .message("Cuenta verificada correctamente")
                .build());
    }

    @PostMapping("/reenviar-codigo")
    public ResponseEntity<AuthResponse> reenviarCodigo(@Valid @RequestBody ReenviarCodigoRequest request) {
        Cliente cliente = clienteService.findByEmail(request.getEmail().trim())
                .orElse(null);

        if (cliente == null) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .message("Email no encontrado")
                    .build());
        }

        if (Integer.valueOf(1).equals(cliente.getVerificado())) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .message("La cuenta ya está verificada")
                    .build());
        }

        cliente.setCodigoVerificacion(generarCodigo6Digitos());
        clienteService.update(cliente);

        boolean enviado = emailUtil.enviarCodigoVerificacion(cliente.getEmail(), cliente.getCodigoVerificacion());

        AuthResponse.AuthResponseBuilder respuesta = AuthResponse.builder().success(true);
        if (enviado) {
            respuesta.message("Se ha reenviado el código de verificación a tu correo");
        } else if (exponerCodigoDesarrollo) {
            respuesta.message("El correo no está configurado; usa el código de verificación mostrado.")
                    .codigoDesarrollo(cliente.getCodigoVerificacion());
        } else {
            respuesta.message("No se pudo reenviar el correo de verificación");
        }
        return ResponseEntity.ok(respuesta.build());
    }

    @GetMapping("/check")
    public ResponseEntity<AuthResponse> check(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteId") == null) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .message("No hay sesión activa")
                    .build());
        }

        Long clienteId = (Long) session.getAttribute("clienteId");
        String rol = (String) session.getAttribute("rol");
        String nombre = (String) session.getAttribute("nombre");

        return ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .message("Sesión activa")
                .rol(rol)
                .clienteId(clienteId)
                .nombre(nombre)
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .message("Sesión cerrada correctamente")
                .build());
    }

    private String generarCodigo6Digitos() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}