package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.WorkingHoursRequestDTO;
import org.example.ultimatecalendarmaven.mapper.WorkingHoursMapper;
import org.example.ultimatecalendarmaven.model.*;
import org.example.ultimatecalendarmaven.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkingHoursService {

    private final WorkingHoursRepository workingHoursRepository;
    private final StaffRepository staffRepository;
    private final TenantRepository tenantRepository;
    private final WorkingHoursMapper mapper;

    @Transactional(readOnly = true)
    public List<WorkingHours> listByStaff(UUID tenantId, UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + staffId));
        if (!staff.getTenant().getId().equals(tenantId))
            throw new IllegalArgumentException("Staff does not belong to tenant");
        // no hay método “por tenant” directo; usamos findAll y filtramos por staff
        return workingHoursRepository.findAll().stream()
                .filter(w -> w.getStaff().getId().equals(staffId))
                .toList();
    }

    public WorkingHours create(UUID tenantId, WorkingHoursRequestDTO dto) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
        Staff staff = staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + dto.getStaffId()));
        if (!staff.getTenant().getId().equals(tenantId))
            throw new IllegalArgumentException("Staff does not belong to tenant");

        WorkingHours wh = mapper.toEntity(dto);
        wh.setTenant(tenant);
        wh.setStaff(staff);
        return workingHoursRepository.save(wh);
    }

    public WorkingHours update(UUID tenantId, UUID id, WorkingHoursRequestDTO dto) {
        WorkingHours wh = workingHoursRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WorkingHours not found: " + id));
        if (!wh.getTenant().getId().equals(tenantId))
            throw new IllegalArgumentException("WorkingHours does not belong to tenant");

        // si cambia staffId, revalidar pertenencia
        if (dto.getStaffId() != null && !dto.getStaffId().equals(wh.getStaff().getId())) {
            Staff staff = staffRepository.findById(dto.getStaffId())
                    .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + dto.getStaffId()));
            if (!staff.getTenant().getId().equals(tenantId))
                throw new IllegalArgumentException("Staff does not belong to tenant");
            wh.setStaff(staff);
        }

        mapper.update(wh, dto);
        return workingHoursRepository.save(wh);
    }

    public boolean delete(UUID tenantId, UUID id) {
        return workingHoursRepository.findById(id)
                .filter(w -> w.getTenant().getId().equals(tenantId))
                .map(w -> { workingHoursRepository.delete(w); return true; })
                .orElse(false);
    }
}