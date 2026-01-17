package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.model.ServiceEntity;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.StaffService;
import org.example.ultimatecalendarmaven.model.StaffServiceId;
import org.example.ultimatecalendarmaven.repository.ServiceRepository;
import org.example.ultimatecalendarmaven.repository.StaffRepository;
import org.example.ultimatecalendarmaven.repository.StaffServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffAssignmentService {

    private final StaffRepository staffRepository;
    private final ServiceRepository serviceRepository;
    private final StaffServiceRepository staffServiceRepository;


    //NO REVISADO
    public void assignServices(UUID tenantId, UUID staffId, List<UUID> serviceId) {
        Staff staff = getStaff(staffId);
        if (!staff.getTenant().getId().equals(tenantId)) {
            throw new IllegalArgumentException("Staff does not belong to tenant: " + tenantId);
        }
        serviceId.stream().forEach(sid -> assignService(tenantId, staff, sid));
    }

    //NO REVISADO
    public void unassignService(UUID tenantId, UUID staffId, UUID serviceId) {
        Staff staff = getStaff(staffId);
        if (!staff.getTenant().getId().equals(tenantId)) {
            throw new IllegalArgumentException("Staff does not belong to tenant: " + tenantId);
        }

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + serviceId));
        if (!service.getTenant().getId().equals(tenantId)) {
            throw new IllegalArgumentException("Service does not belong to tenant: " + tenantId);
        }

        StaffServiceId id = new StaffServiceId(staffId, serviceId);
        if (staffServiceRepository.existsById(id)) {
            staffServiceRepository.deleteById(id);
        }
    }

    public List<ServiceEntity> listServicesForStaff(UUID tenantId, UUID staffId) {
        Staff staff = getStaff(staffId);
        if (!staff.getTenant().getId().equals(tenantId)) {
            throw new IllegalArgumentException("Staff does not belong to tenant: " + tenantId);
        }
        // Usamos un método de repo específico para listar los servicios del staff dentro del tenant
        return staffServiceRepository.findByStaff(staff).stream()
                .map(StaffService::getService)
                .filter(s -> s.getTenant().getId().equals(tenantId))
                .toList();
    }

    private void assignService(UUID tenantId, Staff staff, UUID serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + serviceId));
        if (!service.getTenant().getId().equals(tenantId)) {
            throw new IllegalArgumentException("Service does not belong to tenant: " + tenantId);
        }

        StaffServiceId id = new StaffServiceId(staff.getId(), serviceId);
        if (!staffServiceRepository.existsById(id)) {
            staffServiceRepository.save(new StaffService(staff, service));
        }
        // Si ya existe, idempotente: no haces nada.
    }

    private Staff getStaff(UUID staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + staffId));
        return staff;
    }
}