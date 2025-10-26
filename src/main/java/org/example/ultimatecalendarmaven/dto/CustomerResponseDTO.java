package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerResponseDTO {
    private UUID id;
    private UUID tenantId;
    private String name;
    private String email;
    private String phone;
    private String notes;
    private OffsetDateTime createdAt;
}