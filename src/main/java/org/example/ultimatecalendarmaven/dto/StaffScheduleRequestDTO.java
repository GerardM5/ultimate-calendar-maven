package org.example.ultimatecalendarmaven.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffScheduleRequestDTO {

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
}
