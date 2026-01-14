package org.example.ultimatecalendarmaven.repository;

import org.example.ultimatecalendarmaven.model.Staff;
import org.example.ultimatecalendarmaven.model.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StaffScheduleRepository extends JpaRepository<StaffSchedule, UUID> {

    List<StaffSchedule> findByStaff(Staff staff);

    List<StaffSchedule> findByStaffAndRangeDate(UUID staffId, OffsetDateTime startDate, OffsetDateTime endDate);
}
