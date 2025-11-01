package org.example.ultimatecalendarmaven.repository;

import org.example.ultimatecalendarmaven.model.Appointment;
import org.example.ultimatecalendarmaven.model.Customer;
import org.example.ultimatecalendarmaven.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByStaffAndStartsAtLessThanAndEndsAtGreaterThanAndActiveTrue(Staff staff, OffsetDateTime ends, OffsetDateTime starts);
    List<Appointment> findByCustomerOrderByStartsAtDesc(Customer customer);

    @Query("""
  select a from Appointment a
  join fetch a.staff
  join fetch a.customer
  where a.tenant.id = :tenantId
    and (:from is null or a.endsAt >= :from)
    and (:to   is null or a.startsAt <= :to)
  order by a.startsAt asc
""")
    List<Appointment> findByTenantAndRangeEager(
            @Param("tenantId") UUID tenantId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}
