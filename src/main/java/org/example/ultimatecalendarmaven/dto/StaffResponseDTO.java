package org.example.ultimatecalendarmaven.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffResponseDTO {
    private UUID id;
    private UUID tenantId;
    private String name;
    private String email;
    private String phone;
    private String color;
    private Boolean active;
}