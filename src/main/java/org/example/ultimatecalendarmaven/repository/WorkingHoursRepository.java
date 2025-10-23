package org.example.ultimatecalendarmaven.repository;
import org.example.ultimatecalendarmaven.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.UUID;
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, UUID> {
    List<WorkingHours> findByStaffAndWeekdayOrderByStartTimeAsc(Staff staff, int weekday);
}
