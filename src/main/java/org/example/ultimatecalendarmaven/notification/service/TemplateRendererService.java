package org.example.ultimatecalendarmaven.notification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateRendererService {

    private final ObjectMapper objectMapper;

    public RenderedEmail render(String templateId, String payloadJson) {
        JsonNode p = read(payloadJson);

        if ("TEST_EMAIL".equals(templateId)) {
            String subject = "Ultimate Calendar - Test Email";
            String body = """
            <div style="font-family: Arial, sans-serif; line-height:1.4">
              <h2>Test email ✅</h2>
              <p>If you received this email, SMTP sending is working.</p>
              <p><b>Payload</b>:</p>
              <pre style="background:#f6f8fa;padding:12px;border-radius:8px">%s</pre>
            </div>
            """.formatted(payloadJson);

            return new RenderedEmail(subject, body);
        }

        // MVP templates inline
        if ("APPOINTMENT_CREATED".equals(templateId)) {
            String customer = text(p, "customerName", "Customer");
            String date = text(p, "date", "unknown date");
            String time = text(p, "time", "unknown time");
            String shop = text(p, "barberShop", "Barber shop");

            String subject = "Your appointment is confirmed";
            String body = """
                    Hi %s,

                    Your appointment at %s is confirmed.

                    Date: %s
                    Time: %s

                    See you soon!
                    """.formatted(customer, shop, date, time);

            return new RenderedEmail(subject, body);
        }


        // fallback
        return new RenderedEmail("Notification", "You have a new notification.");
    }

    private JsonNode read(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid payload JSON", e);
        }
    }

    private String text(JsonNode node, String field, String def) {
        JsonNode v = node.get(field);
        return v != null && !v.isNull() ? v.asText() : def;
    }

    public record RenderedEmail(String subject, String body) {}
}