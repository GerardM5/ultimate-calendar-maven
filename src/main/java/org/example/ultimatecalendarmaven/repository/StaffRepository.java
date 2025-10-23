package org.example.ultimatecalendarmaven.repository;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.UUID;
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    List<Staff> findByTenantAndActiveTrue(Tenant tenant);
}
