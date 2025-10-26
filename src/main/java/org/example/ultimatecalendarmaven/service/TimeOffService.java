package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.TimeOffRequestDTO;
import org.example.ultimatecalendarmaven.mapper.TimeOffMapper;
import org.example.ultimatecalendarmaven.model.*;
import org.example.ultimatecalendarmaven.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeOffService {

    private final TimeOffRepository timeOffRepository;
    private final StaffRepository staffRepository;
    private final TenantRepository tenantRepository;
    private final TimeOffMapper mapper;

    @Transactional(readOnly = true)
    public List<TimeOff> listByStaff(UUID tenantId, UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + staffId));
        if (!staff.getTenant().getId().equals(tenantId))
            throw new IllegalArgumentException("Staff does not belong to tenant");
        return timeOffRepository.findAll().stream()
                .filter(t -> t.getStaff().getId().equals(staffId))
                .toList();
    }

    public TimeOff create(UUID tenantId, TimeOffRequestDTO dto) {
        if (!dto.isValid()) throw new IllegalArgumentException("Invalid time range");
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
        Staff staff = staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + dto.getStaffId()));
        if (!staff.getTenant().getId().equals(tenantId))
            throw new IllegalArgumentException("Staff does not belong to tenant");

        TimeOff entity = mapper.toEntity(dto);
        entity.setTenant(tenant);
        entity.setStaff(staff);
        return timeOffRepository.save(entity);
    }

    public TimeOff update(UUID tenantId, UUID id, TimeOffRequestDTO dto) {
        TimeOff entity = timeOffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TimeOff not found: " + id));
        if (!entity.getTenant().getId().equals(tenantId))
            throw new IllegalArgumentException("TimeOff does not belong to tenant");

        if (dto.getStaffId() != null && !dto.getStaffId().equals(entity.getStaff().getId())) {
            Staff staff = staffRepository.findById(dto.getStaffId())
                    .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + dto.getStaffId()));
            if (!staff.getTenant().getId().equals(tenantId))
                throw new IllegalArgumentException("Staff does not belong to tenant");
            entity.setStaff(staff);
        }

        if (dto.getStartsAt() != null && dto.getEndsAt() != null && !dto.isValid())
            throw new IllegalArgumentException("Invalid time range");

        mapper.update(entity, dto);
        return timeOffRepository.save(entity);
    }

    public boolean delete(UUID tenantId, UUID id) {
        return timeOffRepository.findById(id)
                .filter(t -> t.getTenant().getId().equals(tenantId))
                .map(t -> { timeOffRepository.delete(t); return true; })
                .orElse(false);
    }
}