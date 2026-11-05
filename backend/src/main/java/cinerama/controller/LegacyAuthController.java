package cinerama.controller;

import cinerama.dto.AuthResponse;
import cinerama.entity.Cliente;
import cinerama.service.IClienteService;
import cinerama.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LegacyAuthController {

    private final IClienteService clienteService;
    private final PasswordUtil passwordUtil;

    @GetMapping("/ClienteController")
    public ResponseEntity<AuthResponse> legacyLogin(
            @RequestParam String action,
            @RequestParam String dni,
            @RequestParam String contrasena,
            HttpServletRequest request) {

        if (!"login".equals(action)) {
            return ResponseEntity.badRequest().body(
                    AuthResponse.builder().success(false).mensaje("Acción no soportada").build());
        }

        Cliente cliente = clienteService.findByDni(dni).orElse(null);

        if (cliente == null || !passwordUtil.check(contrasena, cliente.getContrasena())) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .mensaje("DNI o contraseña incorrectos")
                    .build());
        }

        if (Integer.valueOf(0).equals(cliente.getActivo())) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .mensaje("La cuenta está desactivada")
                    .build());
        }

        if (Integer.valueOf(0).equals(cliente.getVerificado())) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(false)
                    .needVerify(true)
                    .id(cliente.getIdCliente())
                    .email(cliente.getEmail())
                    .mensaje("La cuenta no ha sido verificada. Revisa tu correo.")
                    .build());
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("clienteId", cliente.getIdCliente());
        session.setAttribute("rol", cliente.getRol());
        session.setAttribute("nombre", cliente.getNombre());

        return ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .mensaje("Inicio de sesión exitoso")
                .rol(cliente.getRol())
                .clienteId(cliente.getIdCliente())
                .nombre(cliente.getNombre())
                .build());
    }
}