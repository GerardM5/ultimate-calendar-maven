package org.example.ultimatecalendarmaven.repository;

import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
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
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    @Query("""
                select s
                from StaffSchedule s
                where s.staff.id in :staffIds
                  and s.startTime < :to
                  and s.endTime > :from
        """)
    List<StaffSchedule> findAllByStaffIdsAndDateRange(List<UUID> staffIds, Instant from, Instant to);
}
