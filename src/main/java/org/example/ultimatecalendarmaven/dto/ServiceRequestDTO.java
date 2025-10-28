package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceRequestDTO {
    @NotNull
    private UUID tenantId;

    @NotBlank
    private String name;

    private String description;

    private String imageUrl;

    @Positive
    private int durationMin;

    @Min(0)
    private int priceCents;

    @Min(0)
    private int bufferBefore;

    @Min(0)
    private int bufferAfter;

    @Builder.Default
    private Boolean active = true;
}