package org.example.ultimatecalendarmaven.repository;
import org.example.ultimatecalendarmaven.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.UUID;
public interface TimeOffRepository extends JpaRepository<TimeOff, UUID> {
    List<TimeOff> findByStaffAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(Staff staff, OffsetDateTime ends, OffsetDateTime starts);
}
