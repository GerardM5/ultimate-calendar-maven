package org.example.ultimatecalendarmaven.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffScheduleRequestDTO {
    private UUID staffId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
