package org.example.ultimatecalendarmaven.notification.dto;

import org.example.ultimatecalendarmaven.notification.model.EmailStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OutboxEmailResponse(
        UUID id,
        UUID tenantId,
        String templateId,
        String toEmail,
        EmailStatus status,
        int attempts,
        LocalDateTime nextAttemptAt,
        LocalDateTime createdAt,
        LocalDateTime sentAt,
        String providerMessageId,
        String lastError
) {}