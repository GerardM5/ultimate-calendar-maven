package org.example.ultimatecalendarmaven.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.ServiceResponseDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleRequestUpdateDTO;
import org.example.ultimatecalendarmaven.dto.StaffScheduleResponseDTO;
import org.example.ultimatecalendarmaven.service.StaffScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Staff Schedule", description = "APIs for managing staff schedules")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class StaffScheduleController {

    @Autowired
    private final StaffScheduleService staffScheduleService;

    @PostMapping
    public ResponseEntity<?> assign(@RequestHeader(name = "X-Tenant-ID") UUID tenantId,
                                    @RequestBody List<StaffScheduleRequestDTO> staffScheduleRequestDTOList) {
        List<StaffScheduleResponseDTO> response = staffScheduleService.assignSchedule(tenantId, staffScheduleRequestDTOList);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping()
    public ResponseEntity<?> list(@RequestHeader(name = "X-Tenant-ID") UUID tenantId,
                                  @RequestParam(required = false) List<UUID> staffId,
                                  //from and to params
                                  @RequestParam(required = false) String from,
                                  @RequestParam(required = false) String to
    ) {
        return ResponseEntity.ok().body(staffScheduleService.getSchedules(tenantId, staffId, from, to));
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestHeader(name = "X-Tenant-ID") UUID tenantId,
                                    @RequestParam UUID staffId,
                                    @RequestBody List<StaffScheduleRequestUpdateDTO> staffScheduleRequestDTOList) {

        return ResponseEntity.ok(
                staffScheduleService
                        .updateSchedule(
                                tenantId,
                                staffId,
                                staffScheduleRequestDTOList)
        );
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestHeader(name = "X-Tenant-ID") UUID tenantId,
                                    @RequestParam UUID scheduleId) {
        staffScheduleService.deleteSchedule(tenantId, scheduleId);
        return ResponseEntity.noContent().build();
    }

}
