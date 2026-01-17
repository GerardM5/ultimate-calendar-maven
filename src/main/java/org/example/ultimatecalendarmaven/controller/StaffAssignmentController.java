package org.example.ultimatecalendarmaven.controller;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.ServiceResponseDTO;
import org.example.ultimatecalendarmaven.mapper.ServiceMapper;
import org.example.ultimatecalendarmaven.service.StaffAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants/{tenantId}/staff/{staffId}")
public class StaffAssignmentController {

    private final StaffAssignmentService staffAssignmentService;
    private final ServiceMapper serviceMapper;

    @PostMapping("/services")
    public ResponseEntity<Void> assign(@PathVariable("tenantId")UUID tenantId,
                                       @PathVariable("staffId") UUID staffId,
                                       @RequestBody List<UUID> serviceIds) {
        staffAssignmentService.assignServices(tenantId, staffId, serviceIds);
        return ResponseEntity.noContent().build(); // 204 idempotente
    }

    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<Void> unassign(@PathVariable("tenantId")UUID tenantId,
                                         @PathVariable("staffId") UUID staffId,
                                         @PathVariable("serviceId") UUID serviceId) {
        staffAssignmentService.unassignService(tenantId, staffId, serviceId);
        return ResponseEntity.noContent().build(); // 204 idempotente
    }

    @GetMapping("/services")
    public List<ServiceResponseDTO> list(@PathVariable UUID tenantId,
                                         @PathVariable UUID staffId) {
        return staffAssignmentService.listServicesForStaff(tenantId, staffId)
                .stream().map(serviceMapper::toResponse).toList();
    }
}