package org.example.ultimatecalendarmaven.dto;

import lombok.*;
import org.example.ultimatecalendarmaven.model.AppointmentStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSummaryDTO {
    private UUID id;
    private StaffResponseDTO staff;
    private CustomerResponseDTO customer;
    private ServiceResponseDTO service;
    private AppointmentStatus status;
    private String notes;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
}