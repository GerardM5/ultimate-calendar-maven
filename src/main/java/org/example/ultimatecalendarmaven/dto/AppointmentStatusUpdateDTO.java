package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import org.example.ultimatecalendarmaven.model.AppointmentStatus;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppointmentStatusUpdateDTO {
    @NotNull
    private AppointmentStatus status;
}