package org.example.ultimatecalendarmaven.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ultimatecalendarmaven.dto.EmailRequest;
import org.example.ultimatecalendarmaven.model.Appointment;
import org.example.ultimatecalendarmaven.model.ChannelType;
import org.example.ultimatecalendarmaven.model.MessageLog;
import org.example.ultimatecalendarmaven.repository.MessageLogRepository;
import org.example.ultimatecalendarmaven.utils.HtmlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final MessageLogRepository messageLogRepository;
    /** Absent when {@code sendgrid.api-key} is not configured. */
    private final Optional<SendGrid> sendGrid;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${sendgrid.from-name}")
    private String fromName;

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Sends an email via the SendGrid API and persists an audit entry in MessageLog.
     * Exceptions are caught and logged so that callers are never interrupted by
     * a delivery failure.
     */
    public void send(EmailRequest request) {
        if (sendGrid.isEmpty()) {
            log.warn("SendGrid is not configured – skipping email to {}", request.getTo());
            return;
        }

        String status = "FAILED";
        try {
            Mail mail = new Mail(
                    new Email(fromEmail, fromName),
                    request.getSubject(),
                    new Email(request.getTo()),
                    new Content("text/html", request.getHtmlContent())
            );

            Request sgRequest = new Request();
            sgRequest.setMethod(Method.POST);
            sgRequest.setEndpoint("mail/send");
            sgRequest.setBody(mail.build());

            Response response = sendGrid.get().api(sgRequest);
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                status = "SENT";
                log.info("Email sent to {} [template={}]", request.getTo(), request.getTemplate());
            } else {
                log.warn("SendGrid responded with {} for {}", response.getStatusCode(), request.getTo());
            }
        } catch (IOException e) {
            log.error("Failed to send email to {}: {}", request.getTo(), e.getMessage(), e);
        } finally {
            saveLog(request, status);
        }
    }

    /**
     * Convenience method that sends an appointment-confirmation email to the
     * customer.  Silently skips when the customer has no email address.
     */
    public void sendAppointmentConfirmation(Appointment appointment) {
        String customerEmail = appointment.getCustomer().getEmail();
        if (customerEmail == null || customerEmail.isBlank()) return;

        send(EmailRequest.builder()
                .tenant(appointment.getTenant())
                .to(customerEmail)
                .subject("Appointment confirmed – " + appointment.getService().getName())
                .htmlContent(buildConfirmationHtml(appointment))
                .template("appointment_confirmation")
                .relatedAppointment(appointment)
                .build());
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void saveLog(EmailRequest request, String status) {
        try {
            MessageLog messageLog = MessageLog.builder()
                    .tenant(request.getTenant())
                    .channel(ChannelType.EMAIL)
                    .toAddress(request.getTo())
                    .template(request.getTemplate())
                    .status(status)
                    .relatedAppointment(request.getRelatedAppointment())
                    .build();
            messageLogRepository.save(messageLog);
        } catch (Exception e) {
            log.error("Failed to persist MessageLog: {}", e.getMessage(), e);
        }
    }

    private String buildConfirmationHtml(Appointment appointment) {
        String customerName = HtmlUtils.escapeHtml(appointment.getCustomer().getName());
        String serviceName  = HtmlUtils.escapeHtml(appointment.getService().getName());
        String staffName    = HtmlUtils.escapeHtml(appointment.getStaff().getName());
        String dateTime     = appointment.getStartsAt().toString();
        String confirmUrl   = baseUrl + "/api/v1/appointments/confirm?token="
                + appointment.getConfirmationToken();

        return "<!DOCTYPE html>"
                + "<html lang=\"es\"><head><meta charset=\"UTF-8\">"
                + "<style>"
                + "body{margin:0;padding:0;background:#f4f6f8;font-family:Arial,sans-serif}"
                + ".wrapper{max-width:560px;margin:40px auto;background:#fff;border-radius:12px;"
                + "box-shadow:0 4px 20px rgba(0,0,0,.08);overflow:hidden}"
                + ".header{background:#1a237e;padding:32px;text-align:center}"
                + ".header h1{margin:0;color:#fff;font-size:22px}"
                + ".body{padding:32px}"
                + ".body p{color:#444;line-height:1.7;margin:0 0 16px}"
                + ".detail{background:#f0f4ff;border-radius:8px;padding:16px;margin:24px 0}"
                + ".detail p{margin:6px 0;color:#333;font-size:14px}"
                + ".detail strong{color:#1a237e}"
                + ".btn-wrap{text-align:center;margin:32px 0}"
                + ".btn{display:inline-block;padding:14px 36px;background:#1a237e;color:#fff;"
                + "text-decoration:none;border-radius:8px;font-size:16px;font-weight:bold}"
                + ".footer{text-align:center;padding:20px;color:#aaa;font-size:12px}"
                + "</style></head>"
                + "<body><div class=\"wrapper\">"
                + "<div class=\"header\"><h1>Confirmación de cita</h1></div>"
                + "<div class=\"body\">"
                + "<p>Hola <strong>" + customerName + "</strong>,</p>"
                + "<p>Has solicitado una cita. Por favor, confírmala haciendo clic en el botón de abajo.</p>"
                + "<div class=\"detail\">"
                + "<p><strong>Servicio:</strong> " + serviceName + "</p>"
                + "<p><strong>Profesional:</strong> " + staffName + "</p>"
                + "<p><strong>Fecha y hora:</strong> " + dateTime + "</p>"
                + "</div>"
                + "<div class=\"btn-wrap\">"
                + "<a href=\"" + confirmUrl + "\" class=\"btn\">Confirmar reserva</a>"
                + "</div>"
                + "<p style=\"font-size:13px;color:#888;\">Si no has solicitado esta cita, puedes ignorar este correo.</p>"
                + "</div>"
                + "<div class=\"footer\">Ultimate Calendar &mdash; Gracias por confiar en nosotros</div>"
                + "</div></body></html>";
    }
}
