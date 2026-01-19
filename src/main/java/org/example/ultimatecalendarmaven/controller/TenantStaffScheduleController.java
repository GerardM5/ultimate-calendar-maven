package org.example.ultimatecalendarmaven.controller;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.StaffScheduleResponseDTO;
import org.example.ultimatecalendarmaven.service.StaffScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants/{tenantId}/schedules")
public class TenantStaffScheduleController {

    private final StaffScheduleService staffScheduleService;

    @GetMapping
    public ResponseEntity<List<StaffScheduleResponseDTO>> filterSchedules(
            @PathVariable UUID tenantId,
            @RequestParam(required = false) List<UUID> staffIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        List<StaffScheduleResponseDTO> schedules = staffScheduleService.filterSchedules(tenantId, staffIds, from, to);
        return ResponseEntity.ok(schedules);
    }
}
