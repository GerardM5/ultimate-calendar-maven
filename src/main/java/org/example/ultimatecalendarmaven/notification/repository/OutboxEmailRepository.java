package org.example.ultimatecalendarmaven.notification.repository;

import org.example.ultimatecalendarmaven.notification.model.EmailStatus;
import org.example.ultimatecalendarmaven.notification.model.OutboxEmail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEmailRepository extends JpaRepository<OutboxEmail, UUID> {

    @Query("""
        SELECT e FROM OutboxEmail e
        WHERE e.status = :status
          AND e.nextAttemptAt <= :now
        ORDER BY e.createdAt ASC
        """)
    List<OutboxEmail> findReadyToSend(EmailStatus status, LocalDateTime now, Pageable pageable);
}