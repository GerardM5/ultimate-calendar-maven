package org.example.ultimatecalendarmaven.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SlotDTO {
    // Rango en UTC (para guardar/reservar sin ambigüedades)
    private OffsetDateTime start; // [start, end)
    private OffsetDateTime end;

    // Opcional: strings en hora local (útiles para UI)
    private String startLocal;
    private String endLocal;
}