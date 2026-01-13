package org.example.ultimatecalendarmaven.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffScheduleResponseDTO {

    private String id;
    private String staffId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
}
