package org.example.ultimatecalendarmaven.notification.sender;

import org.example.ultimatecalendarmaven.notification.model.MailMode;
import org.example.ultimatecalendarmaven.notification.model.OutboxEmail;
import org.example.ultimatecalendarmaven.notification.model.TenantMailSettings;

public interface EmailSender {
    boolean supports(MailMode mode);
    void send(TenantMailSettings settings, OutboxEmail email);
}