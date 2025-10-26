package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TimeOffResponseDTO {
    private UUID id;
    private UUID tenantId;
    private UUID staffId;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
    private String reason;
}