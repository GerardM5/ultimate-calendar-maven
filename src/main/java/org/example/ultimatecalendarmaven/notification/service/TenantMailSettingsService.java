package org.example.ultimatecalendarmaven.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.notification.dto.TenantMailSettingsRequest;
import org.example.ultimatecalendarmaven.notification.mapper.TenantMailSettingsMapper;
import org.example.ultimatecalendarmaven.notification.model.EmailStatus;
import org.example.ultimatecalendarmaven.notification.model.MailMode;
import org.example.ultimatecalendarmaven.notification.model.TenantMailSettings;
import org.example.ultimatecalendarmaven.notification.repository.TenantMailSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantMailSettingsService {

    private final TenantMailSettingsRepository repository;
    private final TenantMailSettingsMapper mapper;

    @Transactional(readOnly = true)
    public TenantMailSettings get(UUID tenantId) {
        return repository.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Mail settings not found for tenantId=" + tenantId));
    }

    @Transactional
    public TenantMailSettings getOrCreate(UUID tenantId) {
        return repository.findByTenantId(tenantId)
                .orElseGet(() -> {
                    TenantMailSettings s = new TenantMailSettings(tenantId);
                    // valores “safe default”
                    s.setMode(MailMode.SHARED_DOMAIN);
                    s.setStatus(EmailStatus.ACTIVE);
                    s.setFromName("Ultimate Calendar");
                    s.setFromEmail("no-reply@ultimate-calendar.local"); // cámbialo luego por tu dominio real
                    s.setReplyTo(null);
                    s.setCreatedAt(LocalDateTime.now());
                    s.setUpdatedAt(LocalDateTime.now());
                    return repository.save(s);
                });
    }

    @Transactional
    public TenantMailSettings update(UUID tenantId, TenantMailSettingsRequest request) {
        TenantMailSettings current = getOrCreate(tenantId);

        current.setFromName(request.fromName());
        current.setFromEmail(request.fromEmail());
        current.setReplyTo(request.replyTo());
        current.setMode(request.mode() != null ? request.mode() : current.getMode());

        // en CUSTOM_DOMAIN podrías dejar status=PENDING hasta que verifique DNS
        if (current.getMode() == MailMode.CUSTOM_DOMAIN && current.getStatus() == EmailStatus.ACTIVE) {
            current.setStatus(EmailStatus.PENDING);
        }

        current.setUpdatedAt(LocalDateTime.now());
        return repository.save(current);
    }
}