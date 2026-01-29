package org.example.ultimatecalendarmaven.notification.sender;

import org.example.ultimatecalendarmaven.notification.model.MailMode;
import org.example.ultimatecalendarmaven.notification.model.OutboxEmail;
import org.example.ultimatecalendarmaven.notification.model.TenantMailSettings;
import org.springframework.stereotype.Component;

@Component
public class SharedDomainEmailSender implements EmailSender {

    @Override
    public boolean supports(MailMode mode) {
        return mode == MailMode.SHARED_DOMAIN;
    }

    @Override
    public void send(TenantMailSettings settings, OutboxEmail email) {
        // aquí metes SES / SendGrid / SMTP centralizado
    }
}