package org.example.ultimatecalendarmaven.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.example.ultimatecalendarmaven.notification.model.TenantMailSettings;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class LogEmailGateway implements EmailGateway {

    @Override
    public GatewayResponse send(TenantMailSettings settings, String toEmail, String subject, String body) {
        log.info("""
                [MAIL][LOG]
                Tenant: {}
                FromName: {}
                FromEmail: {}
                ReplyTo: {}
                To: {}
                Subject: {}
                Body:
                {}
                """,
                settings.getTenantId(),
                settings.getFromName(),
                settings.getFromEmail(),
                settings.getReplyTo(),
                toEmail,
                subject,
                body
        );

        return new GatewayResponse("log-" + UUID.randomUUID());
    }
}