package org.example.ultimatecalendarmaven.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.notification.dto.TenantMailSettingsResponse;
import org.example.ultimatecalendarmaven.notification.mapper.TenantMailSettingsMapper;
import org.example.ultimatecalendarmaven.notification.dto.TenantMailSettingsRequest;
import org.example.ultimatecalendarmaven.notification.service.TenantMailSettingsService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/me/mail-settings")
@RequiredArgsConstructor
public class TenantMailSettingsController {

    private final TenantMailSettingsService service;
    private final TenantMailSettingsMapper mapper;

    @GetMapping
    public TenantMailSettingsResponse get(@RequestHeader("X-Tenant-Id") UUID tenantId) {
        return mapper.toResponse(service.getOrCreate(tenantId));
    }

    @PutMapping
    public TenantMailSettingsResponse update(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody TenantMailSettingsRequest request) {

        return mapper.toResponse(service.update(tenantId, request));
    }
}