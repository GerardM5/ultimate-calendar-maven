package org.example.ultimatecalendarmaven.notification.service;

import org.example.ultimatecalendarmaven.notification.model.TenantMailSettings;

public interface EmailGateway {
    GatewayResponse send(TenantMailSettings settings, String toEmail, String subject, String body);

    record GatewayResponse(String providerMessageId) {}
}