package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkingHoursRequestDTO {
    @NotNull private UUID staffId;        // pertenece al tenant del path
    @Min(1) @Max(7) private int weekday;  // 1=lunes ... 7=domingo
    @NotNull private LocalTime startTime; // hora local del tenant
    @NotNull private LocalTime endTime;

    @AssertTrue(message = "startTime must be before endTime")
    public boolean isValidRange() {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }
}