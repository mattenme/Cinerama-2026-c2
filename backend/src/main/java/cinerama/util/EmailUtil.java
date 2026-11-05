package cinerama.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.DataHandler;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class EmailUtil {

    @Value("${cinerama.mail.host:${spring.mail.host:smtp.gmail.com}}")
    private String host;

    @Value("${cinerama.mail.port:${spring.mail.port:587}}")
    private String port;

    @Value("${cinerama.mail.username:${spring.mail.username:}}")
    private String username;

    @Value("${cinerama.mail.password:${spring.mail.password:}}")
    private String password;

    @Value("${cinerama.mail.from:${spring.mail.from:}}")
    private String from;

    @Value("${cinerama.mail.debug:${spring.mail.properties.mail.debug:true}}")
    private boolean debug;

    private Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", debug);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public boolean enviarCorreo(String to, String subject, String html) {
        try {
            String sender = (from == null || from.isBlank()) ? username : from;
            if (sender == null || sender.isBlank()) {
                System.err.println("No se envió correo: remitente no configurado.");
                return false;
            }

            Message msg = new MimeMessage(getSession());
            msg.setFrom(new InternetAddress(sender));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setContent(html, "text/html; charset=utf-8");
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            System.err.println("Error enviando correo a " + to + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean enviarCodigoVerificacion(String email, String codigo) {
        String html = "<!DOCTYPE html><html><head><meta charset='utf-8'></head><body style='margin:0;padding:0;background:#f5f5f5;font-family:Arial,sans-serif;'>"
            + "<div style='max-width:480px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.1);'>"
            + "<div style='background:#ffc107;padding:20px;text-align:center;'><h2 style='margin:0;color:#1a1a2e;'>Cinerama</h2></div>"
            + "<div style='padding:30px;text-align:center;'>"
            + "<h3 style='color:#333;margin:0 0 10px;'>Verifica tu cuenta</h3>"
            + "<p style='color:#666;font-size:14px;'>Usa este código para completar tu registro:</p>"
            + "<div style='background:#f8f9fc;border-radius:8px;padding:16px;margin:20px 0;letter-spacing:8px;font-size:28px;font-weight:bold;color:#1a1a2e;'>" + codigo + "</div>"
            + "<p style='color:#999;font-size:12px;'>El código expira en 10 minutos.</p>"
            + "</div></div></body></html>";
        return enviarCorreo(email, "Cinerama - Código de verificación", html);
    }

    public boolean enviarConfirmacionCompra(String email, String nombre, String pelicula, String sala, String fecha, String asientos, double total, String qrCode, byte[] qrBytes) {
        try {
            String html = "<!DOCTYPE html><html><head><meta charset='utf-8'></head><body style='margin:0;padding:0;background:#f5f5f5;font-family:Arial,sans-serif;'>"
                + "<div style='max-width:520px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.1);'>"
                + "<div style='background:#ffc107;padding:20px;text-align:center;'><h2 style='margin:0;color:#1a1a2e;'>Cinerama</h2></div>"
                + "<div style='padding:30px;'>"
                + "<h3 style='color:#333;margin:0 0 5px;'>¡Compra confirmada!</h3>"
                + "<p style='color:#666;font-size:14px;margin:0 0 20px;'>Hola <strong>" + nombre + "</strong>, aquí tienes los detalles:</p>"
                + "<table style='width:100%;border-collapse:collapse;font-size:14px;'>"
                + "<tr><td style='padding:8px 0;color:#888;'>Película</td><td style='padding:8px 0;font-weight:600;text-align:right;'>" + pelicula + "</td></tr>"
                + "<tr><td style='padding:8px 0;color:#888;border-top:1px solid #eee;'>Sala</td><td style='padding:8px 0;font-weight:600;text-align:right;border-top:1px solid #eee;'>" + sala + "</td></tr>"
                + "<tr><td style='padding:8px 0;color:#888;border-top:1px solid #eee;'>Fecha / Hora</td><td style='padding:8px 0;font-weight:600;text-align:right;border-top:1px solid #eee;'>" + fecha + "</td></tr>"
                + "<tr><td style='padding:8px 0;color:#888;border-top:1px solid #eee;'>Asientos</td><td style='padding:8px 0;font-weight:600;text-align:right;border-top:1px solid #eee;'>" + asientos + "</td></tr>"
                + "<tr><td style='padding:8px 0;color:#888;border-top:1px solid #eee;'>Total</td><td style='padding:8px 0;font-weight:700;text-align:right;border-top:1px solid #eee;color:#1a1a2e;font-size:16px;'>S/ " + String.format("%.2f", total) + "</td></tr>"
                + "</table>"
                + "<div style='text-align:center;margin:24px 0 0;'>"
                + "<p style='color:#888;font-size:13px;margin:0 0 8px;'>Presenta este código en taquilla</p>"
                + "<img src='cid:qr002' alt='QR' style='width:160px;height:160px;border:2px solid #eee;border-radius:8px;padding:8px;'>"
                + "<div style='font-size:14px;font-weight:bold;letter-spacing:3px;color:#1a1a2e;margin-top:8px;'>" + qrCode + "</div>"
                + "</div></div></div></body></html>";

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(html, "text/html; charset=utf-8");

            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setDataHandler(new DataHandler(new ByteArrayDataSource(qrBytes, "image/png")));
            imagePart.setHeader("Content-ID", "<qr002>");
            imagePart.setDisposition(MimeBodyPart.INLINE);

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(htmlPart);
            mp.addBodyPart(imagePart);

            String sender = (from == null || from.isBlank()) ? username : from;
            if (sender == null || sender.isBlank()) {
                System.err.println("No se envió correo de compra: remitente no configurado.");
                return false;
            }

            Message msg = new MimeMessage(getSession());
            msg.setFrom(new InternetAddress(sender));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            msg.setSubject("Cinerama - Confirmación de compra");
            msg.setContent(mp);
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            System.err.println("Error enviando correo a " + email + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}