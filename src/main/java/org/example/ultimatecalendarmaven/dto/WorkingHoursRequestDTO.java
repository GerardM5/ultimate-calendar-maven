package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkingHoursRequestDTO {
    @NotNull private UUID staffId;        // pertenece al tenant del path
    @Min(0) @Max(6) private int weekday;  // 0=domingo ... 6=sábado
    @NotNull private LocalTime startTime; // hora local del tenant
    @NotNull private LocalTime endTime;

    @AssertTrue(message = "startTime must be before endTime")
    public boolean isValidRange() {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }
}