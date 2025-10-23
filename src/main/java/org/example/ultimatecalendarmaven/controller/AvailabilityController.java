package org.example.ultimatecalendarmaven.controller;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.AvailabilityRequestDTO;
import org.example.ultimatecalendarmaven.dto.SlotDTO;
import org.example.ultimatecalendarmaven.service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants/{tenantId}")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/availability")
    public List<SlotDTO> availability(
            @PathVariable UUID tenantId,
            @RequestParam UUID serviceId,
            @RequestParam(required = false) UUID staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day
    ) {
        var req = AvailabilityRequestDTO.builder()
                .tenantId(tenantId)
                .serviceId(serviceId)
                .staffId(staffId)
                .day(day)
                .build();
        return availabilityService.getAvailability(req);
    }
}