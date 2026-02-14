package org.example.ultimatecalendarmaven.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffScheduleResponseDTO {

    private String id;
    private StaffResponseDTO staff;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
