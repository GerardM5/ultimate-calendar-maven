package org.example.ultimatecalendarmaven.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffScheduleRequestUpdateDTO {

    private UUID id;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
}
