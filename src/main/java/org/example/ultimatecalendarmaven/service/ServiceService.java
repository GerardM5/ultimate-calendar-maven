package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.ServiceRequestDTO;
import org.example.ultimatecalendarmaven.mapper.ServiceMapper;
import org.example.ultimatecalendarmaven.model.ServiceEntity;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.example.ultimatecalendarmaven.repository.ServiceRepository;
import org.example.ultimatecalendarmaven.repository.StaffRepository;
import org.example.ultimatecalendarmaven.repository.StaffServiceRepository;
import org.example.ultimatecalendarmaven.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final TenantRepository tenantRepository;
    private final StaffRepository staffRepository;
    private final ServiceMapper serviceMapper;
    private final StaffServiceRepository staffServiceRepository;

    public List<ServiceEntity> findByTenant(UUID tenantId) {
        var tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
        return serviceRepository.findByTenantAndActiveTrue(tenant);
    }

    public ServiceEntity getOrThrow(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + id));
    }

    public ServiceEntity create(ServiceRequestDTO dto) {
        Tenant tenant = tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + dto.getTenantId()));

        // Unicidad por (tenant, name) a nivel app además del UNIQUE en DB
        serviceRepository.findAll().stream()
                .filter(s -> s.getTenant().getId().equals(tenant.getId()))
                .filter(s -> s.getName().equalsIgnoreCase(dto.getName()))
                .findFirst()
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Service name already exists for tenant: " + dto.getName());
                });

        ServiceEntity entity = serviceMapper.toEntity(dto);
        entity.setTenant(tenant);
        return serviceRepository.save(entity);
    }

    public ServiceEntity update(UUID id, ServiceRequestDTO dto) {
        ServiceEntity entity = getOrThrow(id);

        // si viene tenantId, reasigna tenant
        if (dto.getTenantId() != null && !dto.getTenantId().equals(entity.getTenant().getId())) {
            Tenant tenant = tenantRepository.findById(dto.getTenantId())
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + dto.getTenantId()));
            entity.setTenant(tenant);
        }

        // si cambia el nombre, comprobar unicidad dentro del tenant
        if (dto.getName() != null && !dto.getName().equalsIgnoreCase(entity.getName())) {
            UUID tenantId = entity.getTenant().getId();
            boolean exists = serviceRepository.findAll().stream()
                    .anyMatch(s -> s.getTenant().getId().equals(tenantId)
                            && s.getName().equalsIgnoreCase(dto.getName())
                            && !s.getId().equals(entity.getId()));
            if (exists) {
                throw new IllegalArgumentException("Service name already exists for tenant: " + dto.getName());
            }
        }

        serviceMapper.update(entity, dto);
        return serviceRepository.save(entity);
    }

    public void delete(UUID id) {
        serviceRepository.deleteById(id);
    }
}