package org.example.ultimatecalendarmaven.controller;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.StaffRequestDTO;
import org.example.ultimatecalendarmaven.dto.StaffResponseDTO;
import org.example.ultimatecalendarmaven.mapper.StaffMapper;
import org.example.ultimatecalendarmaven.service.StaffService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/tenant/{tenantId}")
    public List<StaffResponseDTO> getByTenant(@PathVariable UUID tenantId) {
        return staffService.findByTenant(tenantId).stream()
                .map(staffMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> getById(@PathVariable UUID id) {
        return staffService.findById(id)
                .map(staffMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StaffResponseDTO> create(@RequestBody StaffRequestDTO staff) {
        var saved = staffService.create(staff);
        return ResponseEntity.created(URI.create("/api/v1/staff/" + saved.getId()))
                .body(staffMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> update(@PathVariable UUID id,
                                                   @RequestBody StaffRequestDTO request) {
        var updated = staffService.update(id, request);
        return ResponseEntity.ok(staffMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (staffService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}