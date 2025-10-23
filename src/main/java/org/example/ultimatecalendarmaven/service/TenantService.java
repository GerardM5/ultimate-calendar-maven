package org.example.ultimatecalendarmaven.service;

import lombok.RequiredArgsConstructor;
import org.example.ultimatecalendarmaven.dto.TenantRequestDTO;
import org.example.ultimatecalendarmaven.mapper.TenantMapper;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.example.ultimatecalendarmaven.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    public Tenant getOrThrow(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + id));
    }

    public Tenant create(TenantRequestDTO dto) {
        // Unicidad de slug a nivel de app (además del UNIQUE en DB)
        tenantRepository.findBySlug(dto.getSlug()).ifPresent(t ->
                { throw new IllegalArgumentException("Slug already exists: " + dto.getSlug()); });

        Tenant entity = tenantMapper.toEntity(dto);
        return tenantRepository.save(entity);
    }

    public Tenant update(UUID id, TenantRequestDTO dto) {
        Tenant entity = getOrThrow(id);

        // Si cambia el slug, volvemos a comprobar unicidad
        if (dto.getSlug() != null && !dto.getSlug().equals(entity.getSlug())) {
            tenantRepository.findBySlug(dto.getSlug()).ifPresent(t -> {
                if (!t.getId().equals(id)) {
                    throw new IllegalArgumentException("Slug already exists: " + dto.getSlug());
                }
            });
        }

        tenantMapper.update(entity, dto);
        return tenantRepository.save(entity);
    }

    public void delete(UUID id) {
        tenantRepository.deleteById(id);
    }
}