package org.example.ultimatecalendarmaven.repository;
import org.example.ultimatecalendarmaven.model.ServiceEntity;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.UUID;
public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {
    List<ServiceEntity> findByTenantAndActiveTrue(Tenant tenant);
}
