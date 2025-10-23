package org.example.ultimatecalendarmaven.repository;

import org.example.ultimatecalendarmaven.model.AuditLog;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTenantOrderByCreatedAtDesc(Tenant tenant);
}
