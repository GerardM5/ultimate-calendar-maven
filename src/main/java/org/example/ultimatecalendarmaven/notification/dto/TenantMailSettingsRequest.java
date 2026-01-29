package org.example.ultimatecalendarmaven.notification.dto;

import org.example.ultimatecalendarmaven.notification.model.MailMode;

public record TenantMailSettingsRequest(
        String fromName,
        String fromEmail,
        String replyTo,
        MailMode mode
) {}