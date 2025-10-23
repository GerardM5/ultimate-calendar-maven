package org.example.ultimatecalendarmaven.repository;

import org.example.ultimatecalendarmaven.model.Customer;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.UUID;
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByTenantAndEmail(Tenant tenant, String email);
    Optional<Customer> findByTenantAndPhone(Tenant tenant, String phone);
}
