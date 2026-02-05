package org.example.ultimatecalendarmaven.repository;

import org.example.ultimatecalendarmaven.model.Appointment;
import org.example.ultimatecalendarmaven.model.Customer;
import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.Tenant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
public interface AppointmentRepository extends JpaRepository<Appointment, UUID>,
        JpaSpecificationExecutor<Appointment> {
    List<Appointment> findByStaffAndStartsAtLessThanAndEndsAtGreaterThanAndActiveTrue(Staff staff, OffsetDateTime ends, OffsetDateTime starts);

    List<Appointment> findByTenantAndStartsAtLessThanAndStartsAtGreaterThan(Tenant tenant, OffsetDateTime ends, OffsetDateTime starts);

    //find by tenant and startat between dates
    @EntityGraph(attributePaths = {"staff", "staff.tenant", "customer", "service"})
    List<Appointment> findByTenantAndStartsAtBetweenOrderByStartsAtAsc(Tenant tenant, OffsetDateTime from, OffsetDateTime to);
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

    boolean existsByStaffAndStartsAtLessThanAndEndsAtGreaterThanAndActiveTrue(Staff staff, OffsetDateTime endsAt, OffsetDateTime startsAt);
}
