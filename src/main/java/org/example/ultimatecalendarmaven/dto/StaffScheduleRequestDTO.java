package org.example.ultimatecalendarmaven.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffScheduleRequestDTO {
    private UUID staffId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
}
