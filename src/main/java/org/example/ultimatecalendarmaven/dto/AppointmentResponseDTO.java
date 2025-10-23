package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import org.example.ultimatecalendarmaven.model.AppointmentStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    private UUID id;
    private UUID tenantId;
    private UUID serviceId;
    private UUID staffId;
    private UUID customerId;
    private AppointmentStatus status;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
    private Integer priceCents;
    private String notes;
    private OffsetDateTime createdAt;
}