package org.example.ultimatecalendarmaven.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.AppointmentConfirmed;
import org.example.ultimatecalendarmaven.model.Appointment;
import org.example.ultimatecalendarmaven.service.AppointmentService;
import org.example.ultimatecalendarmaven.utils.HtmlUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Appointments", description = "APIs for managing appointments")
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentConfirmController {

    private final AppointmentService appointmentService;

    @GetMapping(value = "/confirm", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> confirm(@RequestParam String token) {
        try {
            AppointmentConfirmed appt = appointmentService.confirmByToken(token);
            String html = buildConfirmedHtml(appt);
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
        } catch (IllegalArgumentException e) {
            String html = buildErrorHtml("The confirmation link is invalid or expired.");
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_HTML).body(html);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String buildConfirmedHtml(AppointmentConfirmed appt) {

        return "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\">"
                + "<title>Reserva confirmada</title>"
                + "<style>"
                + "body{font-family:Arial,sans-serif;background:#f4f6f8;display:flex;justify-content:center;align-items:center;min-height:100vh;margin:0}"
                + ".card{background:#fff;border-radius:12px;padding:40px;max-width:480px;width:100%;box-shadow:0 4px 20px rgba(0,0,0,.1);text-align:center}"
                + ".icon{font-size:56px;margin-bottom:16px}"
                + "h1{color:#2e7d32;margin:0 0 12px}"
                + "p{color:#555;line-height:1.6}"
                + ".detail{background:#f0f4ff;border-radius:8px;padding:16px;margin:24px 0;text-align:left}"
                + ".detail p{margin:6px 0;color:#333}"
                + ".detail strong{color:#1a237e}"
                + "</style></head><body>"
                + "<div class=\"card\">"
                + "<div class=\"icon\">✅</div>"
                + "<h1>¡Reserva confirmada!</h1>"
                + "<p>Hola <strong>" + appt.customerName() + "</strong>, tu reserva ha sido confirmada con éxito.</p>"
                + "<div class=\"detail\">"
                + "<p><strong>Servicio:</strong> " + appt.serviceName() + "</p>"
                + "<p><strong>Profesional:</strong> " + appt.staffName() + "</p>"
                + "<p><strong>Fecha y hora:</strong> " + appt.date() +" "+appt.time() + "</p>"
                + "</div>"
                + "<p>¡Te esperamos!</p>"
                + "</div></body></html>";
    }

    private String buildErrorHtml(String message) {
        return "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\">"
                + "<title>Enlace inválido</title>"
                + "<style>"
                + "body{font-family:Arial,sans-serif;background:#f4f6f8;display:flex;justify-content:center;align-items:center;min-height:100vh;margin:0}"
                + ".card{background:#fff;border-radius:12px;padding:40px;max-width:480px;width:100%;box-shadow:0 4px 20px rgba(0,0,0,.1);text-align:center}"
                + ".icon{font-size:56px;margin-bottom:16px}"
                + "h1{color:#c62828;margin:0 0 12px}"
                + "p{color:#555}"
                + "</style></head><body>"
                + "<div class=\"card\">"
                + "<div class=\"icon\">❌</div>"
                + "<h1>Enlace inválido</h1>"
                + "<p>" + HtmlUtils.escapeHtml(message) + "</p>"
                + "</div></body></html>";
    }
}
