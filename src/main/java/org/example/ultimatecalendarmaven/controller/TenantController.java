package org.example.ultimatecalendarmaven.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.*;
import org.example.ultimatecalendarmaven.mapper.TenantMapper;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.example.ultimatecalendarmaven.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Tenant", description = "APIs for managing tenants")
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;
    private final TenantMapper tenantMapper;

    @GetMapping
    public List<TenantResponseDTO> list() {
        return tenantService.findAll().stream()
                .map(tenantMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantResponseDTO> get(@PathVariable UUID id) {
        Tenant tenant = tenantService.getOrThrow(id);
        return ResponseEntity.ok(tenantMapper.toResponse(tenant));
    }

    @PostMapping
    public ResponseEntity<TenantResponseDTO> create(@Validated @RequestBody TenantRequestDTO dto) {
        Tenant saved = tenantService.create(dto);
        return ResponseEntity
                .created(URI.create("/api/v1/tenants/" + saved.getId()))
                .body(tenantMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantResponseDTO> update(@PathVariable UUID id,
                                                    @RequestBody TenantRequestDTO dto) {
        Tenant updated = tenantService.update(id, dto);
        return ResponseEntity.ok(tenantMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tenantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}