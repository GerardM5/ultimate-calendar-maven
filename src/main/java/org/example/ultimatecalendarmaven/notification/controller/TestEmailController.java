package org.example.ultimatecalendarmaven.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.notification.dto.TestEmailRequest;
import org.example.ultimatecalendarmaven.notification.service.EmailOutboxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tenants/me/mail")
public class TestEmailController {

    private final EmailOutboxService emailOutboxService;

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> sendTestEmail(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody TestEmailRequest request
    ) {
        // payload simple para el template
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sentAt", OffsetDateTime.now().toString());
        payload.put("tenantId", tenantId.toString());
        payload.put("message", "If you received this email, SMTP sending is working ✅");

        var outboxEmail = emailOutboxService.enqueue(
                tenantId,
                "TEST_EMAIL",
                request.toEmail(),
                payload
        );

        // Devolvemos el id para poder rastrearlo en DB/logs
        return ResponseEntity.accepted().body(Map.of(
                "status", "QUEUED",
                "outboxEmailId", outboxEmail.getId(),
                "tenantId", tenantId,
                "toEmail", request.toEmail(),
                "templateId", "TEST_EMAIL"
        ));
    }
}