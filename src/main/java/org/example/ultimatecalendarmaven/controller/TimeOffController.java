package org.example.ultimatecalendarmaven.controller;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.*;
import org.example.ultimatecalendarmaven.mapper.TimeOffMapper;
import org.example.ultimatecalendarmaven.service.TimeOffService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants/{tenantId}")
public class TimeOffController {

    private final TimeOffService service;
    private final TimeOffMapper mapper;

    @GetMapping("/staff/{staffId}/time-off")
    public List<TimeOffResponseDTO> listByStaff(@PathVariable UUID tenantId,
                                                @PathVariable UUID staffId) {
        return service.listByStaff(tenantId, staffId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/time-off")
    public ResponseEntity<TimeOffResponseDTO> create(@PathVariable UUID tenantId,
                                                     @Validated @RequestBody TimeOffRequestDTO dto) {
        var saved = service.create(tenantId, dto);
        return ResponseEntity.created(URI.create("/api/v1/tenants/%s/staff/%s/time-off"
                        .formatted(tenantId, saved.getStaff().getId())))
                .body(mapper.toResponse(saved));
    }

    @PutMapping("/time-off/{id}")
    public ResponseEntity<TimeOffResponseDTO> update(@PathVariable UUID tenantId,
                                                     @PathVariable UUID id,
                                                     @Validated @RequestBody TimeOffRequestDTO dto) {
        var updated = service.update(tenantId, id, dto);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/time-off/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID tenantId, @PathVariable UUID id) {
        return service.delete(tenantId, id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}