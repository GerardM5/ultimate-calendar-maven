package org.example.ultimatecalendarmaven.repository;

import org.example.ultimatecalendarmaven.model.ChannelType;
import org.example.ultimatecalendarmaven.model.Consent;
import org.example.ultimatecalendarmaven.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.UUID;
public interface ConsentRepository extends JpaRepository<Consent, UUID> {
    Optional<Consent> findByCustomerAndPurposeAndChannel(Customer customer, String purpose, ChannelType channel);
}
