package org.example.ultimatecalendarmaven.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentFilter(
        OffsetDateTime from,
        OffsetDateTime to,
        UUID staffId
) {}