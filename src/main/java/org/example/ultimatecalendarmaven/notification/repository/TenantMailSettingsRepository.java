package org.example.ultimatecalendarmaven.notification.repository;

import org.example.ultimatecalendarmaven.notification.model.TenantMailSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantMailSettingsRepository extends JpaRepository<TenantMailSettings, UUID> {

    Optional<TenantMailSettings> findByTenantId(UUID tenantId);
}