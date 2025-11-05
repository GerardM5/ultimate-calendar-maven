package org.example.ultimatecalendarmaven.dto;

import lombok.*;
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
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
}