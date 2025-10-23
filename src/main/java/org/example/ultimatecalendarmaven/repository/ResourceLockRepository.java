package org.example.ultimatecalendarmaven.repository;

import org.example.ultimatecalendarmaven.model.ResourceLock;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.UUID;
public interface ResourceLockRepository extends JpaRepository<ResourceLock, UUID> {
    List<ResourceLock> findByTenantAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(Tenant tenant, OffsetDateTime ends, OffsetDateTime starts);
    List<ResourceLock> findByStaffAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(Staff staff, OffsetDateTime ends, OffsetDateTime starts);
}
