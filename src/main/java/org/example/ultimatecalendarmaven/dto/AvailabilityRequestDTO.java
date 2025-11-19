package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AvailabilityRequestDTO {
    @NotNull private UUID tenantId;
    @NotNull private UUID serviceId;
    private UUID staffId;           // opcional: si null, puedes devolver slots agregados o forzar staff
    private OffsetDateTime from;     // opcional: si null, usar fecha actual

}