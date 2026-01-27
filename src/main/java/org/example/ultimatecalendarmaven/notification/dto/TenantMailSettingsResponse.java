package org.example.ultimatecalendarmaven.notification.dto;

import org.example.ultimatecalendarmaven.notification.model.EmailStatus;
import org.example.ultimatecalendarmaven.notification.model.MailMode;

import java.time.LocalDateTime;
import java.util.UUID;

public record TenantMailSettingsResponse(
        UUID tenantId,
        MailMode mode,
        String fromName,
        String fromEmail,
        String replyTo,
        String providerIdentityId,
        EmailStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}