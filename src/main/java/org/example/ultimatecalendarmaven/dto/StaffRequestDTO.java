package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffRequestDTO {
    @NotNull
    private UUID tenantId;

    @NotBlank
    private String name;

    private String email;
    private String phone;
    private String color;
    @Builder.Default
    private Boolean active = true;
}