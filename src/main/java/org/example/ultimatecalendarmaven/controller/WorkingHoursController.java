package org.example.ultimatecalendarmaven.controller;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.*;
import org.example.ultimatecalendarmaven.mapper.WorkingHoursMapper;
import org.example.ultimatecalendarmaven.service.WorkingHoursService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants/{tenantId}")
public class WorkingHoursController {

    private final WorkingHoursService service;
    private final WorkingHoursMapper mapper;

    @GetMapping("/staff/{staffId}/working-hours")
    public List<WorkingHoursResponseDTO> listByStaff(@PathVariable UUID tenantId,
                                                     @PathVariable UUID staffId) {
        return service.listByStaff(tenantId, staffId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/working-hours")
    public ResponseEntity<WorkingHoursResponseDTO> create(@PathVariable UUID tenantId,
                                                          @Validated @RequestBody WorkingHoursRequestDTO dto) {
        var saved = service.create(tenantId, dto);
        return ResponseEntity.created(URI.create("/api/v1/tenants/%s/staff/%s/working-hours"
                        .formatted(tenantId, saved.getStaff().getId())))
                .body(mapper.toResponse(saved));
    }

    @PutMapping("/working-hours/{id}")
    public ResponseEntity<WorkingHoursResponseDTO> update(@PathVariable UUID tenantId,
                                                          @PathVariable UUID id,
                                                          @Validated @RequestBody WorkingHoursRequestDTO dto) {
        var updated = service.update(tenantId, id, dto);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/working-hours/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID tenantId, @PathVariable UUID id) {
        return service.delete(tenantId, id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}