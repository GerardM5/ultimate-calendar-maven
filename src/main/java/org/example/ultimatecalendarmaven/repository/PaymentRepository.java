package org.example.ultimatecalendarmaven.repository;

import org.example.ultimatecalendarmaven.model.Payment;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.UUID;
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByTenantOrderByCreatedAtDesc(Tenant tenant);
}
