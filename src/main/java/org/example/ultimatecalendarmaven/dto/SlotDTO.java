package org.example.ultimatecalendarmaven.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SlotDTO {

    private StaffResponseDTO staff;

    // Rango en UTC (para guardar/reservar sin ambigüedades)
    private OffsetDateTime start; // [start, end)
    private OffsetDateTime end;

    // Opcional: strings en hora local (útiles para UI)
    private String startLocal;
    private String endLocal;
}