package org.example.ultimatecalendarmaven.controller;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.StaffRequestDTO;
import org.example.ultimatecalendarmaven.dto.StaffResponseDTO;
import org.example.ultimatecalendarmaven.mapper.ServiceMapper;
import org.example.ultimatecalendarmaven.mapper.StaffMapper;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.service.StaffAssignmentService;
import org.example.ultimatecalendarmaven.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final StaffMapper staffMapper;
    private final StaffAssignmentService staffAssignmentService;
    private final ServiceMapper serviceMapper;

    @GetMapping("/tenant/{tenantId}")
    public List<StaffResponseDTO> getByTenant(@PathVariable UUID tenantId) {
        return staffService.findByTenant(tenantId).stream()
                .map(staff -> toResponseWithServices(staff, tenantId))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> getById(@PathVariable UUID id) {
        return staffService.findById(id)
                .map(staff -> {
                    UUID tenantId = staff.getTenant().getId();
                    return toResponseWithServices(staff, tenantId);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody StaffRequestDTO staffRequest) {
        var saved = staffService.create(staffRequest);
        UUID tenantId = saved.getTenant().getId();
        return ResponseEntity.created(URI.create("/api/v1/staff/" + saved.getId()))
                .body(toResponseWithServices(saved, tenantId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> update(@PathVariable UUID id,
                                                   @RequestBody StaffRequestDTO request) {
        var updated = staffService.update(id, request);
        UUID tenantId = updated.getTenant().getId();
        return ResponseEntity.ok(toResponseWithServices(updated, tenantId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (staffService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private StaffResponseDTO toResponseWithServices(Staff staff, UUID tenantId) {
        var dto = staffMapper.toResponse(staff);
        var services = staffAssignmentService.listServicesForStaff(tenantId, staff.getId());
        dto.setServices(services.stream()
                .map(serviceMapper::toResponse)
                .toList());
        return dto;
    }

}