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
        return "<h2>Appointment confirmed</h2>"
                + "<p>Hello " + escapeHtml(appointment.getCustomer().getName()) + ",</p>"
                + "<p>Your appointment for <strong>" + escapeHtml(appointment.getService().getName()) + "</strong>"
                + " with " + escapeHtml(appointment.getStaff().getName())
                + " on " + appointment.getStartsAt() + " has been confirmed.</p>"
                + "<p>Thank you for choosing us.</p>";
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
}
