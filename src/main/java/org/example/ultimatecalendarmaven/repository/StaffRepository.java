package org.example.ultimatecalendarmaven.repository;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.*;
import java.util.UUID;
public interface StaffRepository extends JpaRepository<Staff, UUID>,
        JpaSpecificationExecutor<Staff> {
    List<Staff> findByTenantAndActiveTrue(Tenant tenant);

    Optional<Staff> findByIdAndTenantId(UUID id, UUID tenantId);
}
