package org.example.ultimatecalendarmaven.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceResponseDTO {
    private UUID id;
    private UUID tenantId;
    private String name;
    private String description;
    private String imageUrl;
    private int durationMin;
    private int priceCents;
    private int bufferBefore;
    private int bufferAfter;
}