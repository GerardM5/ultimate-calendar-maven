package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TenantRequestDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String slug;

    @Builder.Default
    private String timezone = "Europe/Madrid";
}