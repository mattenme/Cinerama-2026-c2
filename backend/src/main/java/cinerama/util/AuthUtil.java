package cinerama.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class AuthUtil {

    public static boolean esAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        String rol = getRol(req);
        return rol != null && rol.equalsIgnoreCase("ADMIN");
    }

    public static boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("clienteId") != null;
    }

    public static Long getClienteId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object id = session.getAttribute("clienteId");
        return id instanceof Long ? (Long) id : null;
    }

    public static String getRol(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object rol = session.getAttribute("rol");
        return rol instanceof String ? (String) rol : null;
    }
}