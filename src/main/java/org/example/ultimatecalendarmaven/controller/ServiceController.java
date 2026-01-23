package org.example.ultimatecalendarmaven.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.ServiceRequestDTO;
import org.example.ultimatecalendarmaven.dto.ServiceResponseDTO;
import org.example.ultimatecalendarmaven.mapper.ServiceMapper;
import org.example.ultimatecalendarmaven.service.ServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Service", description = "APIs for managing services")
@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;
    private final ServiceMapper serviceMapper;

    @GetMapping("/tenants/{tenantId}")
    public List<ServiceResponseDTO> list(@PathVariable UUID tenantId) {
        return serviceService.findByTenant(tenantId).stream()
                .map(serviceMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> get(@PathVariable UUID id) {
        return ResponseEntity.ok(serviceMapper.toResponse(serviceService.getOrThrow(id)));
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> create(@Validated @RequestBody ServiceRequestDTO dto) {
        var saved = serviceService.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/services/" + saved.getId()))
                .body(serviceMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> update(@PathVariable UUID id,
                                                     @RequestBody ServiceRequestDTO dto) {
        var updated = serviceService.update(id, dto);
        return ResponseEntity.ok(serviceMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}