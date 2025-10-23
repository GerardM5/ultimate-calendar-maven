package org.example.ultimatecalendarmaven.repository;

import org.example.ultimatecalendarmaven.model.MessageLog;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.UUID;
public interface MessageLogRepository extends JpaRepository<MessageLog, UUID> {
    List<MessageLog> findByTenantOrderByCreatedAtDesc(Tenant tenant);
}
