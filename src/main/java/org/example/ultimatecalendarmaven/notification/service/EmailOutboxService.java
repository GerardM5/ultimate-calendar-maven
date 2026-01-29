package org.example.ultimatecalendarmaven.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.model.Appointment;
import org.example.ultimatecalendarmaven.notification.model.EmailStatus;
import org.example.ultimatecalendarmaven.notification.model.OutboxEmail;
import org.example.ultimatecalendarmaven.notification.repository.OutboxEmailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailOutboxService {

    private final OutboxEmailRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OutboxEmail enqueue(UUID tenantId, String templateId, String toEmail, Object payload) {
        String json = toJson(payload);
        OutboxEmail email = new OutboxEmail(tenantId, templateId, toEmail, json);
        return repository.save(email);
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not serialize email payload", e);
        }
    }

    public void sendAppointmentConfirmationEmail(Appointment saved) {
        OutboxEmail email = new OutboxEmail();
        email.setTenantId(saved.getTenant().getId());
        email.setToEmail(saved.getCustomer().getEmail());
        email.setTemplateId("appointment-confirmation");
        email.setPayload(toJson(
                Map.of(
                        "appointmentId", saved.getId().toString(),
                        "customerName", saved.getCustomer().getName(),
                        "serviceName", saved.getService().getName(),
                        "staffName", saved.getStaff().getName(),
                        "startsAt", saved.getStartsAt().toString(),
                        "endsAt", saved.getEndsAt().toString()
                )));
        email.setStatus(EmailStatus.PENDING);
        email.setAttempts(0);
        email.setNextAttemptAt(LocalDateTime.now());
        email.setCreatedAt(LocalDateTime.now());
        repository.save(email);
    }
}