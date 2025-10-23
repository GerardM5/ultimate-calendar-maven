package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TenantResponseDTO {
    private UUID id;
    private String name;
    private String slug;
    private String timezone;
    private OffsetDateTime createdAt;
}