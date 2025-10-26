package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import java.time.LocalTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkingHoursResponseDTO {
    private UUID id;
    private UUID tenantId;
    private UUID staffId;
    private int weekday;           // 0..6
    private LocalTime startTime;   // hora local
    private LocalTime endTime;
}