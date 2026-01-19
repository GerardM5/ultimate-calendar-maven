package org.example.ultimatecalendarmaven.repository;

import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StaffScheduleRepository extends JpaRepository<StaffSchedule, UUID> {

    List<StaffSchedule> findByStaff(Staff staff);

    @Query("""
  select s
  from StaffSchedule s
  where s.staff.id = :staffId
    and s.startTime <= :end
    and s.endTime >= :start
""")
    List<StaffSchedule> findOverlapping(
            @Param("staffId") UUID staffId,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );

    @Query("""
            select s
            from StaffSchedule s
            where s.staff.tenant.id = :tenantId
              and (:staffIds is null or :staffIds is empty or s.staff.id in :staffIds)
              and (:from is null or s.endTime >= :from)
              and (:to is null or s.startTime <= :to)
            """)
    List<StaffSchedule> findByTenantAndStaffIdsAndDateRange(
            @Param("tenantId") UUID tenantId,
            @Param("staffIds") List<UUID> staffIds,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}
