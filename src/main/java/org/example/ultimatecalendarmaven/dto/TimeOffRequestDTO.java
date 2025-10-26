package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TimeOffRequestDTO {
    @NotNull private UUID staffId;           // pertenece al tenant del path
    @NotNull private OffsetDateTime startsAt; // UTC
    @NotNull private OffsetDateTime endsAt;   // UTC
    private String reason;

    public boolean isValid() {
        return startsAt != null && endsAt != null && startsAt.isBefore(endsAt);
    }
}