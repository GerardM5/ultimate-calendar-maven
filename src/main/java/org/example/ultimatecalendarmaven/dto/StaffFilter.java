package org.example.ultimatecalendarmaven.dto;

import java.util.UUID;

public record StaffFilter(
        String name,
        Boolean active,
        UUID serviceId
) {}