package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.CustomerRequestDTO;
import org.example.ultimatecalendarmaven.mapper.CustomerMapper;
import org.example.ultimatecalendarmaven.model.Customer;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.example.ultimatecalendarmaven.repository.CustomerRepository;
import org.example.ultimatecalendarmaven.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TenantRepository tenantRepository;
    private final CustomerMapper customerMapper;

    @Transactional(readOnly = true)
    public List<Customer> findByTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
        // si tienes un método específico en repo, úsalo; si no, filtra:
        return customerRepository.findAll().stream()
                .filter(c -> c.getTenant().getId().equals(tenant.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Customer getScoped(UUID tenantId, UUID id) {
        return customerRepository.findById(id)
                .filter(c -> c.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    public Customer create(UUID tenantId, CustomerRequestDTO dto) {
        // forzamos coherencia
        dto.setTenantId(tenantId);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        // unicidad email/phone por tenant (además del UNIQUE en DB)
        if (dto.getEmail() != null) {
            customerRepository.findByTenantAndEmail(tenant, dto.getEmail())
                    .ifPresent(c -> { throw new IllegalArgumentException("Email already exists for tenant"); });
        }
        if (dto.getPhone() != null) {
            customerRepository.findByTenantAndPhone(tenant, dto.getPhone())
                    .ifPresent(c -> { throw new IllegalArgumentException("Phone already exists for tenant"); });
        }

        Customer entity = customerMapper.toEntity(dto);
        entity.setTenant(tenant);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        }
        return customerRepository.save(entity);
    }

    public Customer update(UUID tenantId, UUID id, CustomerRequestDTO dto) {
        Customer entity = getScoped(tenantId, id);

        // Si cambia email/phone, revalida unicidad
        if (dto.getEmail() != null && !dto.getEmail().equals(entity.getEmail())) {
            customerRepository.findByTenantAndEmail(entity.getTenant(), dto.getEmail())
                    .ifPresent(other -> {
                        if (!other.getId().equals(entity.getId()))
                            throw new IllegalArgumentException("Email already exists for tenant");
                    });
        }
        if (dto.getPhone() != null && !dto.getPhone().equals(entity.getPhone())) {
            customerRepository.findByTenantAndPhone(entity.getTenant(), dto.getPhone())
                    .ifPresent(other -> {
                        if (!other.getId().equals(entity.getId()))
                            throw new IllegalArgumentException("Phone already exists for tenant");
                    });
        }

        customerMapper.update(entity, dto);
        return customerRepository.save(entity);
    }

    public boolean delete(UUID tenantId, UUID id) {
        return customerRepository.findById(id)
                .filter(c -> c.getTenant().getId().equals(tenantId))
                .map(c -> { customerRepository.delete(c); return true; })
                .orElse(false);
    }
}