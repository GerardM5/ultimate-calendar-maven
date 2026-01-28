package org.example.ultimatecalendarmaven.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ultimatecalendarmaven.model.Appointment;
import org.example.ultimatecalendarmaven.notification.model.EmailStatus;
import org.example.ultimatecalendarmaven.notification.model.OutboxEmail;
import org.example.ultimatecalendarmaven.notification.model.TenantMailSettings;
import org.example.ultimatecalendarmaven.notification.repository.OutboxEmailRepository;
import org.example.ultimatecalendarmaven.notification.repository.TenantMailSettingsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final OutboxEmailRepository outboxRepo;
    private final TenantMailSettingsRepository settingsRepo;
    private final TemplateRendererService renderer;
    private final EmailGateway emailGateway;

    @Scheduled(fixedDelayString = "${mail.outbox.poll-ms:5000}")
    @Transactional
    public void processOutbox() {
        log.info("[MAIL][WORKER] tick {}", java.time.OffsetDateTime.now());
        List<OutboxEmail> batch = outboxRepo.findReadyToSend(
                EmailStatus.PENDING,
                LocalDateTime.now(),
                PageRequest.of(0, 50)
        );

        if (batch.isEmpty()) {
            return;
        }

        log.info("Processing {} outbox emails", batch.size());

        for (OutboxEmail email : batch) {
            try {
                TenantMailSettings settings = settingsRepo.findByTenantId(email.getTenantId())
                        .orElseThrow(() -> new IllegalStateException("Missing TenantMailSettings for tenantId=" + email.getTenantId()));

                TemplateRendererService.RenderedEmail rendered =
                        renderer.render(email.getTemplateId(), email.getPayload());

                EmailGateway.GatewayResponse resp = emailGateway.send(
                        settings,
                        email.getToEmail(),
                        rendered.subject(),
                        rendered.body()
                );

                email.setProviderMessageId(resp.providerMessageId());
                email.setStatus(EmailStatus.SENT);
                email.setSentAt(LocalDateTime.now());
                email.setLastError(null);

            } catch (Exception ex) {
                int attempts = email.getAttempts() + 1;
                email.setAttempts(attempts);
                email.setLastError(ex.getMessage());

                // backoff simple (5 min)
                email.setNextAttemptAt(LocalDateTime.now().plusMinutes(5));

                if (attempts >= 5) {
                    email.setStatus(EmailStatus.FAILED);
                    log.warn("Email {} marked as FAILED after {} attempts. Error: {}", email.getId(), attempts, ex.getMessage());
                } else {
                    log.warn("Email {} failed attempt {}. Will retry. Error: {}", email.getId(), attempts, ex.getMessage());
                }
            }

            outboxRepo.save(email);
        }
    }

    public void sendAppointmentConfirmationEmail(Appointment saved) {
        OutboxEmail email = new OutboxEmail();
        email.setTenantId(saved.getTenant().getId());
        email.setToEmail(saved.getCustomer().getEmail());
        email.setTemplateId("appointment-confirmation");
        email.setPayload(
                Map.of(
                        "appointmentId", saved.getId().toString(),
                        "customerName", saved.getCustomer().getName(),
                        "serviceName", saved.getService().getName(),
                        "staffName", saved.getStaff().getName(),
                        "startsAt", saved.getStartsAt().toString(),
                        "endsAt", saved.getEndsAt().toString()
                ).toString()
        );
        email.setStatus(EmailStatus.PENDING);
        email.setAttempts(0);
        email.setNextAttemptAt(LocalDateTime.now());

        outboxRepo.save(email);
    }
}