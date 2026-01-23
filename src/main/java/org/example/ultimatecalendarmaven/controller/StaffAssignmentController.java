package org.example.ultimatecalendarmaven.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.ServiceResponseDTO;
import org.example.ultimatecalendarmaven.mapper.ServiceMapper;
import org.example.ultimatecalendarmaven.service.StaffAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Staff Assignment", description = "APIs for assigning and unassigning services to staff members")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants/{tenantId}/staff/{staffId}")
public class StaffAssignmentController {

    private final StaffAssignmentService staffAssignmentService;
    private final ServiceMapper serviceMapper;

    @PutMapping("/services")
    public ResponseEntity<?> assign(@PathVariable("tenantId")UUID tenantId,
                                       @PathVariable("staffId") UUID staffId,
                                       @RequestBody List<UUID> serviceIds) {
        staffAssignmentService.replaceServices(tenantId, staffId, serviceIds);
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