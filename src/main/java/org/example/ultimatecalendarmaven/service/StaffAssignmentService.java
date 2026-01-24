package org.example.ultimatecalendarmaven.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.ServiceIdsDTO;
import org.example.ultimatecalendarmaven.model.ServiceEntity;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.StaffService;
import org.example.ultimatecalendarmaven.model.StaffServiceId;
import org.example.ultimatecalendarmaven.repository.ServiceRepository;
import org.example.ultimatecalendarmaven.repository.StaffRepository;
import org.example.ultimatecalendarmaven.repository.StaffServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffAssignmentService {

    private final StaffRepository staffRepository;
    private final ServiceRepository serviceRepository;
    private final StaffServiceRepository staffServiceRepository;
    @PersistenceContext
    private EntityManager entityManager;

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

    @Transactional
    public void replaceServices(UUID tenantId, UUID staffId, ServiceIdsDTO serviceIds) {
        Set<UUID> requested = serviceIds == null ? Set.of() : new HashSet<>(serviceIds.getServiceIds());

        // 1) Validar que el staff existe y pertenece al tenant (sin traer toda la entidad si no hace falta)
        Staff staff = staffRepository.findByIdAndTenantId(staffId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Staff " + staffId + " does not belong to tenant " + tenantId));

        // 2) Validar que los services existen y pertenecen al tenant (en bloque)
        if (!requested.isEmpty()) {
            Set<UUID> found = new HashSet<>(serviceRepository.findIdsByTenantIdAndIdIn(tenantId, requested));
            if (found.size() != requested.size()) {
                Set<UUID> missing = new HashSet<>(requested);
                missing.removeAll(found);
                throw new IllegalArgumentException("Some services do not exist (or not in tenant): " + missing);
            }
        }

        // 3) Leer asignaciones actuales (solo IDs) y calcular diff
        Set<UUID> current = new HashSet<>(staffServiceRepository.findServiceIdsByStaffId(staffId));

        Set<UUID> toRemove = new HashSet<>(current);
        toRemove.removeAll(requested);

        Set<UUID> toAdd = new HashSet<>(requested);
        toAdd.removeAll(current);

        // 4) Aplicar cambios en bloque
        if (!toRemove.isEmpty()) {
            staffServiceRepository.deleteByStaffIdAndServiceIdIn(staffId, toRemove);
        }

        if (!toAdd.isEmpty()) {
            // Insertar StaffService sin cargar ServiceEntity completa: usamos getReference
            List<StaffService> links = new ArrayList<>(toAdd.size());
            for (UUID serviceId : toAdd) {
                ServiceEntity serviceRef = entityManager.getReference(ServiceEntity.class, serviceId);
                links.add(new StaffService(staff, serviceRef));
            }
            staffServiceRepository.saveAll(links);
        }
    }
}