package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.StaffRequestDTO;
import org.example.ultimatecalendarmaven.mapper.StaffMapper;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.repository.StaffRepository;
import org.example.ultimatecalendarmaven.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;
    private final TenantService tenantService;

    public List<Staff> findByTenant(UUID tenantId) {
        return staffRepository.findByTenantAndActiveTrue(tenantService.getOrThrow(tenantId));
    }

    public Optional<Staff> findById(UUID id) {
        return staffRepository.findById(id);
    }

    public Staff create(StaffRequestDTO dto) {
        var staff = staffMapper.toEntity(dto);
        var tenant = tenantService.getOrThrow(dto.getTenantId());
        staff.setTenant(tenant);
        return staffRepository.save(staff);
    }

    public Staff update(UUID id, StaffRequestDTO dto) {
        var staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + id));
        // si viene tenantId, reasignamos tenant
        if (dto.getTenantId() != null) {
            var tenant = tenantService.getOrThrow(dto.getTenantId());
            staff.setTenant(tenant);
        }
        // actualizamos resto de campos ignorando nulls
        staffMapper.update(staff, dto);
        return staffRepository.save(staff);
    }

    public boolean deleteById(UUID id) {
        if (staffRepository.existsById(id)) {
            staffRepository.deleteById(id);
            return true;
        }
        return false;
    }
}