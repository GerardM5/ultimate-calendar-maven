package org.example.ultimatecalendarmaven.controller;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.ServiceResponseDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestDTO;
import org.example.ultimatecalendarmaven.service.StaffScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants/{tenantId}/staff/{staffId}/schedule")
public class StaffScheduleController {

    @Autowired
    private final StaffScheduleService staffScheduleService;

    @PostMapping
    public ResponseEntity<Void> assign(@PathVariable("tenantId") UUID tenantId,
                                       @PathVariable("staffId") UUID staffId,
                                       @RequestBody List<StaffScheduleRequestDTO> staffScheduleRequestDTOList) {
        staffScheduleService.assignSchedule(tenantId, staffId, staffScheduleRequestDTOList);
        return ResponseEntity.noContent().build(); // 204 idempotente
    }

    @GetMapping()
    public ResponseEntity<?> list(@PathVariable UUID tenantId,
                                         @PathVariable UUID staffId) {
        return ResponseEntity.ok().body(staffScheduleService.listScheduleForStaff(tenantId, staffId));
    }



}
