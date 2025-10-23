package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AvailabilityRequestDTO {
    @NotNull private UUID tenantId;
    @NotNull private UUID serviceId;
    private UUID staffId;           // opcional: si null, puedes devolver slots agregados o forzar staff
    @NotNull private LocalDate day; // fecha en zona local del tenant (p. ej. 2025-10-23)
}