package org.example.ultimatecalendarmaven.repository;
import org.example.ultimatecalendarmaven.model.ServiceEntity;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;
import java.util.UUID;
public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {
    List<ServiceEntity> findByTenantAndActiveTrue(Tenant tenant);

    @Query("select s.id from ServiceEntity s where s.tenant.id = :tenantId and s.id in :ids")
    List<UUID> findIdsByTenantIdAndIdIn(@Param("tenantId") UUID tenantId, @Param("ids") Set<UUID> ids);
}
