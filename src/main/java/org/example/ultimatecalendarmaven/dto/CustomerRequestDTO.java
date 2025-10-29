package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerRequestDTO {

    @NotNull
    private UUID tenantId;   // se forzará desde el path

    @NotBlank
    private String name;

    @Email
    private String email;

    @Pattern(regexp = "^[+0-9\\s()-]{6,}$", message = "invalid phone format")
    private String phone;

}