package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSummaryDTO {
    private UUID id;
    private UUID staffId;
    private UUID customerId;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
    private String staffName;
    private String customerName;
}