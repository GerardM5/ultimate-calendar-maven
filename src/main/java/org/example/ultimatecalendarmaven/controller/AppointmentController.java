package org.example.ultimatecalendarmaven.controller;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.AppointmentRequestDTO;
import org.example.ultimatecalendarmaven.dto.AppointmentResponseDTO;
import org.example.ultimatecalendarmaven.dto.AppointmentSummaryDTO;
import org.example.ultimatecalendarmaven.dto.AppointmentStatusUpdateDTO;
import org.example.ultimatecalendarmaven.mapper.AppointmentMapper;
import org.example.ultimatecalendarmaven.model.Appointment;
import org.example.ultimatecalendarmaven.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @GetMapping
    public List<AppointmentSummaryDTO> list(
            @PathVariable UUID tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        return appointmentMapper.toSummaryList(appointmentService.findByTenantAndRange(tenantId, from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> get(@PathVariable UUID tenantId,
                                                      @PathVariable UUID id) {
        return appointmentService.findByIdScoped(tenantId, id)
                .map(appointmentMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@PathVariable UUID tenantId,
                                    @Validated @RequestBody AppointmentRequestDTO dto)  {
        // fuerza coherencia tenant en DTO
        dto.setTenantId(tenantId);
        try {
            Appointment saved = appointmentService.create(dto);
            return ResponseEntity
                    .created(URI.create("/api/v1/tenants/%s/appointments/%s".formatted(tenantId, saved.getId())))
                    .body(appointmentMapper.toResponse(saved));
        } catch (Exception e) {// ---- Error de conflicto semántico (409) y añado un mensaje ----
            return ResponseEntity.status(409).body(e.getMessage());

        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateStatus(@PathVariable UUID tenantId,
                                                               @PathVariable UUID id,
                                                               @Validated @RequestBody AppointmentStatusUpdateDTO body) {
        Appointment updated = appointmentService.updateStatus(tenantId, id, body.getStatus());
        return ResponseEntity.ok(appointmentMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID tenantId, @PathVariable UUID id) {
        return appointmentService.deleteScoped(tenantId, id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}