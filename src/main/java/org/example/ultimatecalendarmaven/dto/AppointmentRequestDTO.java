package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {
    private UUID tenantId;
    private UUID serviceId;
    private UUID staffId;
    private CustomerRequestDTO customer;
    private OffsetDateTime startsAt;
    private String notes;
}