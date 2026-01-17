package org.example.ultimatecalendarmaven.repository;
import org.example.ultimatecalendarmaven.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;
public interface StaffServiceRepository extends JpaRepository<StaffService, StaffServiceId> {
    List<StaffService> findByStaff(Staff staff);
    List<StaffService> findByService(ServiceEntity service);

    @Query("select ss.service.id from StaffService ss where ss.staff.id = :staffId")
    List<UUID> findServiceIdsByStaffId(@Param("staffId") UUID staffId);

    @Modifying
    @Query("delete from StaffService ss where ss.staff.id = :staffId and ss.service.id in :serviceIds")
    int deleteByStaffIdAndServiceIdIn(@Param("staffId") UUID staffId, @Param("serviceIds") Set<UUID> serviceIds);
}
