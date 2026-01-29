package org.example.ultimatecalendarmaven.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ultimatecalendarmaven.notification.model.TenantMailSettings;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@Primary
public class SmtpEmailGateway implements EmailGateway {

    private final JavaMailSender mailSender;

    public SmtpEmailGateway(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public GatewayResponse send(
            TenantMailSettings settings,
            String toEmail,
            String subject,
            String body
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            String fromEmail = settings.getFromEmail() != null
                    ? settings.getFromEmail()
                    : System.getProperty("mail.smtp.fromFallback", "no-reply@tuapp.com");

            String fromName = settings.getFromName() != null
                    ? settings.getFromName()
                    : System.getProperty("mail.smtp.fromNameFallback", "Ultimate Calendar");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML enabled

            if (settings.getReplyTo() != null && !settings.getReplyTo().isBlank()) {
                helper.setReplyTo(settings.getReplyTo());
            }

            mailSender.send(message);

            String providerId = "smtp-" + UUID.randomUUID();
            log.info("SMTP email sent. to={}, subject={}, id={}", toEmail, subject, providerId);

            return new GatewayResponse(providerId);

        } catch (Exception e) {
            log.error("SMTP send failed. to={}, subject={}", toEmail, subject, e);
            throw new RuntimeException("SMTP send failed", e);
        }
    }
}